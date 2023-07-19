package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Common;
import com.habday.server.classes.UIDCreation;
import com.habday.server.config.retrofit.RestInterface;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.payment.Payment;
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
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.habday.server.constants.code.ExceptionCode.*;
import static com.habday.server.constants.state.ScheduledPayState.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService extends Common {
    private final IamportService iamportService;
    private final UIDCreation uidCreation;
    private final RestInterface restService;

    @Transactional
    public GetBillingKeyResponseDto getBillingKey(NoneAuthPayBillingKeyRequestDto billingKeyRequest, Long memberId){
        String cardNumber = billingKeyRequest.getCard_number();
        Payment existingPayment = paymentRepository.findByCardNumberEnd(cardNumber.substring(15, 19));

        if (existingPayment != null)
            throw new CustomException(CARD_ALREADY_EXIST);

        String customer_uid = uidCreation.createCustomerUid(memberId);
        IamportResponse<BillingCustomer> iamportResponse = iamportService.getBillingKeyFromIamport(billingKeyRequest, customer_uid);
        log.info("iamportResponse: " + new Gson().toJson(iamportResponse));

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
        log.info("paymentId: " + paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(NO_PAYMENT_EXIST));
        IamportResponse<BillingCustomer> iamportResponse = iamportService.deleteBillingKeyFromIamport(payment.getBillingKey(),
                "사용자의 요청으로 인한 카드 삭제", "extra none");
        log.info("iamportResponse: " + new Gson().toJson(iamportResponse));
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

    @Transactional
    public UnscheduleResponseDto noneAuthPayUnschedule(NoneAuthPayUnscheduleRequestDto unscheduleRequestDto){
        log.info("noneAuthPayUnschedule start");
        FundingMember fundingMember = fundingMemberRepository.findById(unscheduleRequestDto.getFundingMemberId())
                .orElseThrow(() -> new CustomException(NO_FUNDING_MEMBER_ID));
        FundingItem fundingItem = fundingItemRepository.findById(fundingMember.getFundingItem().getId())
                .orElseThrow(()-> new CustomException(NO_FUNDING_ITEM_ID));
        BigDecimal cancelableAmount = fundingMember.getAmount().subtract(fundingMember.getCancelAmount());
        if (LocalDate.now().compareTo(fundingItem.getFinishDate())>0){
            log.info("펀딩 취소 가능 날짜가 지남");
            throw new CustomException(DELETE_PARTICIPATE_UNAVAILABLE);
        }

        if (cancelableAmount.compareTo(BigDecimal.ZERO) == 0) {//이미 환불 완료됨
            log.info("noneAuthPayUnschedule: 이미 환불 완료");
            throw new CustomException(ALREADY_CANCELED);
        }
        IamportResponse<List<Schedule>> iamportResponse = iamportService.unscheduleFromIamport(fundingMember.getPaymentId(), fundingMember.getMerchantId());
        log.info("noneAuthPayUnschedule: 5" + new Gson().toJson(iamportResponse));
        if(iamportResponse.getCode() != 0){
            log.info("noneAuthPayUnschedule: 아이앰포트 응답 오류");
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

    public void unscheduleAll(FundingItem fundingItem){
        List<Long> fundingMemberId = fundingMemberRepository.getFundingItemIdMatchesFundingItem(fundingItem);
        fundingMemberId.forEach(id -> {
            log.info("unschedulePayment: start");
            NoneAuthPayUnscheduleRequestDto request = new NoneAuthPayUnscheduleRequestDto(id,  "목표 달성 실패로 인한 결제 취소");
            Call<UnscheduleResponseDto> call = restService.unscheduleApi(request);//예약결제 취소 후 fundingMember status cancel로 업데이트
            try {
                Response<UnscheduleResponseDto> response = call.execute();//각각의 요청에 대해서만 익셉션이 생길 테니까 익셉션이 이 함수까지는 안오겠지,,?
                log.info("unschedulePayment response: " + new Gson().toJson(response.body()));
            } catch (IOException e) {
                log.info("unschedule Paymentretrofit 오류: " + e);  //throw new CustomException(FAIL_WHILE_UNSCHEDULING);

            } catch(RuntimeException e){
                log.info("unschedule Payment 서비스 내 오류: " + e);
            }
        });
    }

    public IamportResponse<ScheduleList> showSchedules(ShowSchedulesRequestDto showSchedulesRequestDto){
        IamportResponse<ScheduleList> iamportResponse = iamportService.showSchedulesFromIamport(showSchedulesRequestDto);
        return iamportResponse;
    }
}
