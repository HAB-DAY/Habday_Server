package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.constants.ExceptionCode;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.Payment;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.dto.req.iamport.NoneAuthPayBillingKeyRequest;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.habday.server.dto.res.iamport.GetBillingKeyResponseDto;
import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto.PaymentList;
import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto;
import com.habday.server.exception.CustomException;
import com.habday.server.exception.CustomExceptionWithMessage;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.BillingCustomerData;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.habday.server.constants.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyIamportService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final IamportClient iamportClient =
            new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");


    private String createCustomerUid(Long memberId){
        Long paymentNum = paymentRepository.countByMemberId(memberId)+1;
        return "cus_m" + memberId + "_p" + paymentNum;//ex) cus_m2_p2
    }

    private String createMerchantUid(Long fundingItemId, Long memberId){
        Long itemNum = fundingItemRepository.countByIdAndMemberId(fundingItemId, memberId) + 1;
        return "mer_f" + fundingItemId + "_m" + memberId + "_i" + itemNum;//특정 아이템에 멤버 참여 횟수 정하기 ex)mer_f1_m2_i2
    }
    @Transactional
    public GetBillingKeyResponseDto getBillingKey(NoneAuthPayBillingKeyRequest billingKeyRequest, Long memberId){
        String cardNumber = billingKeyRequest.getCard_number();
        Payment existingPayment = paymentRepository.findByCardNumberEnd(cardNumber.substring(15, 19));

        if (existingPayment != null)
            throw new CustomException(CARD_ALREADY_EXIST);

        String customer_uid = createCustomerUid(memberId);
        IamportResponse<BillingCustomer> iamportResponse = getBillingKeyFromIamport(billingKeyRequest, customer_uid);
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

    private IamportResponse<BillingCustomer> getBillingKeyFromIamport(NoneAuthPayBillingKeyRequest billingKeyRequest, String customer_uid){
        BillingCustomerData billingCustomerData = new BillingCustomerData(
                customer_uid, billingKeyRequest.getCard_number(),
                billingKeyRequest.getExpiry(), billingKeyRequest.getBirth());

        billingCustomerData.setPwd2Digit(billingKeyRequest.getPwd_2digit());
        billingCustomerData.setPg("nice.nictest04m");

        try {
            return iamportClient.postBillingCustomer(customer_uid, billingCustomerData);
        } catch (IOException e) {
            throw new CustomException(BILLING_KEY_INTERNAL_ERROR);
        } catch (IamportResponseException e) {
            throw new CustomException(BILLING_KEY_INTERNAL_ERROR);
        }
    }

    public GetPaymentListsResponseDto getPaymentLists(Long memberId){
        List<PaymentList> paymentLists =  paymentRepository.findByMemberId(memberId);
        if (paymentLists == null){
            return GetPaymentListsResponseDto.of(null);//return 등록된 결제정보 없음
        }

        return GetPaymentListsResponseDto.of(paymentLists);
    }

    public IamportResponse<List<Schedule>> noneAuthPaySchedule(NoneAuthPayScheduleRequestDto scheduleRequestDto){
        ScheduleEntry scheduleEntry= new ScheduleEntry(
                scheduleRequestDto.getMerchant_uid(), scheduleRequestDto.getSchedule_at(), scheduleRequestDto.getAmount());
        scheduleEntry.setName(scheduleRequestDto.getName());
        scheduleEntry.setBuyerName(scheduleRequestDto.getBuyer_name());
        scheduleEntry.setBuyerTel(scheduleRequestDto.getBuyer_tel());
        scheduleEntry.setBuyerEmail(scheduleRequestDto.getBuyer_email());

        ScheduleData scheduleData = new ScheduleData(scheduleRequestDto.getCustomer_uid());
        scheduleData.addSchedule(scheduleEntry);
        try {
            return iamportClient.subscribeSchedule(scheduleData);
        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //todo 매우 중요!!! 아이앰포트 서버의 결과를 반영해야 함. 저장 결과가 이상하면 에러 던지고 롤백 해야 함
        //이 결과를 fundingService에 반환해 거기서 db에 저장
    }
}
