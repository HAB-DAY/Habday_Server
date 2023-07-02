package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.UIDCreation;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.Payment;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.dto.req.iamport.CallbackScheduleRequestDto;
import com.habday.server.dto.req.iamport.NoneAuthPayBillingKeyRequestDto;
import com.habday.server.dto.req.iamport.NoneAuthPayUnscheduleRequestDto;
import com.habday.server.dto.req.iamport.ShowSchedulesRequestDto;
import com.habday.server.dto.res.iamport.DeleteBillingKeyResponseDto;
import com.habday.server.dto.res.iamport.GetBillingKeyResponseDto;
import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto.PaymentList;
import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto;
import com.habday.server.dto.res.iamport.UnscheduleResponseDto;
import com.habday.server.exception.CustomException;
import com.habday.server.exception.CustomExceptionWithMessage;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import com.siot.IamportRestClient.response.ScheduleList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.habday.server.constants.ExceptionCode.*;
import static com.habday.server.constants.ScheduledPayState.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final FundingMemberRepository fundingMemberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final IamportService iamportService;
    private final UIDCreation uidCreation;

    @Transactional
    public GetBillingKeyResponseDto getBillingKey(NoneAuthPayBillingKeyRequestDto billingKeyRequest, Long memberId){
        String cardNumber = billingKeyRequest.getCard_number();
        Payment existingPayment = paymentRepository.findByCardNumberEnd(cardNumber.substring(15, 19));

        if (existingPayment != null)
            throw new CustomException(CARD_ALREADY_EXIST);

        String customer_uid = uidCreation.createCustomerUid(memberId);
        IamportResponse<BillingCustomer> iamportResponse = iamportService.getBillingKeyFromIamport(billingKeyRequest, customer_uid);
        log.debug("iamportResponse: " + new Gson().toJson(iamportResponse));

        if(iamportResponse.getCode() != 0){
            throw new CustomExceptionWithMessage(GET_BILLING_KEY_FAIL, iamportResponse.getMessage());
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        paymentRepository.save(Payment.builder()
                .paymentName(billingKeyRequest.getPayment_name())
                .billingKey(customer_uid)
                .member(member)
                .cardNumberEnd(cardNumber.substring(15, 19))
                .build());

        return GetBillingKeyResponseDto.of(billingKeyRequest.getPayment_name(), customer_uid, iamportResponse.getCode(), iamportResponse.getMessage());
    }

    public DeleteBillingKeyResponseDto deleteBillingKey(Long paymentId){
        log.debug("paymentId: " + paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(NO_PAYMENT_EXIST));
        IamportResponse<BillingCustomer> iamportResponse = iamportService.deleteBillingKeyFromIamport(payment.getBillingKey(),
                "사용자의 요청으로 인한 카드 삭제", "extra none");
        log.debug("iamportResponse: " + new Gson().toJson(iamportResponse));
        if(iamportResponse.getCode() != 0){
            throw new CustomExceptionWithMessage(DELETING_BILLING_KEY_FAIL_INTERNAL_ERROR, iamportResponse.getMessage());
        }

        paymentRepository.delete(payment);
        return DeleteBillingKeyResponseDto.builder()
                .isDelete(true)
                .paymentName(payment.getPaymentName())
                .cardNumberEnd(payment.getCardNumberEnd())
                .paymentId(paymentId)
                .build();
    }

    public GetPaymentListsResponseDto getPaymentLists(Long memberId){
        List<PaymentList> paymentLists =  paymentRepository.findByMemberId(memberId);
        if (paymentLists == null){
            return GetPaymentListsResponseDto.of(null);//return 등록된 결제정보 없음
        }

        return GetPaymentListsResponseDto.of(paymentLists);
    }

    public UnscheduleResponseDto noneAuthPayUnschedule(NoneAuthPayUnscheduleRequestDto unscheduleRequestDto, Long memberId){
        FundingMember fundingMember = fundingMemberRepository.findById(unscheduleRequestDto.getFundingMemberId())
                .orElseThrow(() -> new CustomException(NO_FUNDING_MEMBER_ID));
        FundingItem fundingItem = fundingItemRepository.findById(fundingMember.getFundingItem().getId())
                .orElseThrow(()-> new CustomException(NO_FUNDING_ITEM_ID));

        BigDecimal cancelableAmount = fundingMember.getAmount().subtract(fundingMember.getCancelAmount());

        if (cancelableAmount.compareTo(BigDecimal.ZERO) == 0) {//이미 환불 완료됨
            throw new CustomException(ALREADY_CANCELED);
        }

        IamportResponse<List<Schedule>> iamportResponse = iamportService.unscheduleFromIamport(fundingMember.getPaymentId(), fundingMember.getMerchantId());

        if(iamportResponse.getCode() != 0){
            throw new CustomExceptionWithMessage(PAY_SCHEDULING_INTERNAL_ERROR, iamportResponse.getMessage());
        }

        LocalDate cancelDate = LocalDate.now();
        fundingMember.updateCancel(fundingMember.getAmount(), unscheduleRequestDto.getReason(), cancel, cancelDate);

        return UnscheduleResponseDto.builder()
                .merchant_uid(fundingMember.getMerchantId())
                .merchant_name(fundingItem.getFundingName())
                .cancelDate(cancelDate)
                .amount(fundingMember.getCancelAmount())
                .build();
    }

    public IamportResponse<ScheduleList> showSchedules(ShowSchedulesRequestDto showSchedulesRequestDto){
        IamportResponse<ScheduleList> iamportResponse = iamportService.showSchedulesFromIamport(showSchedulesRequestDto);
        return iamportResponse;
    }

    public void callbackSchedule(CallbackScheduleRequestDto callbackRequestDto, HttpServletRequest request){
        String clientIp = getIp(request);
        String[] ips = {"52.78.100.19", "52.78.48.223", "52.78.5.241"};
        List<String> ipLists = new ArrayList<>(Arrays.asList(ips));

        FundingMember fundingMember = fundingMemberRepository.findByMerchantId(callbackRequestDto.getMerchant_uid());

        IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportService.paymentByImpUid(callbackRequestDto.getImp_uid());

        if (!ipLists.contains(clientIp)){
            fundingMember.updateWebhookFail(fail, "ip주소가 맞지 않음");
            throw new CustomException(UNAUTHORIZED_IP);
        }

        if(!fundingMember.getAmount().equals(response.getResponse().getAmount())){
            fundingMember.updateWebhookFail(fail, "결제 금액이 맞지 않음");
            throw new CustomException(NO_CORRESPONDING_AMOUNT);
        }

        if(callbackRequestDto.getStatus() == paid.getMsg()){
            fundingMember.updateWebhookSuccess(paid);
        }else{
            fundingMember.updateWebhookFail(fail, response.getResponse().getFailReason());
            throw new CustomExceptionWithMessage(WEBHOOK_FAIL, response.getResponse().getFailReason());
        }
    }


    private String getIp(HttpServletRequest request) {
        String [] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", };

        String ip = request.getHeader("X-Forwarded-For");
        for(String header: headers){
            if (ip == null){
                ip = request.getHeader(header);
                log.info(">>>> " + header + " : " + ip);
            }
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;

    }
}
