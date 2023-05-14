package com.habday.server.service;

import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.Payment;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.dto.req.iamport.NoneAuthPayBillingKeyRequest;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.habday.server.exception.CustomException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.BillingCustomerData;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.request.UnscheduleData;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.habday.server.constants.ExceptionCode.*;
import static com.habday.server.constants.ExceptionCode.PAY_UNSCHEDULING_INTERNAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final FundingMemberRepository fundingMemberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final IamportClient iamportClient =
            new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");
    public IamportResponse<BillingCustomer> getBillingKeyFromIamport(NoneAuthPayBillingKeyRequest billingKeyRequest, String customer_uid){
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
            throw new CustomException(PAY_SCHEDULING_INTERNAL_ERROR);
        } catch (IOException e) {
            throw new CustomException(PAY_SCHEDULING_INTERNAL_ERROR);
        }
    }

    public IamportResponse<List<Schedule>> unscheduleFromIamport(Long paymentId, String merchant_uid){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(NO_PAYMENT_EXIST));

        UnscheduleData unscheduleData = new UnscheduleData(payment.getBillingKey());//paymentId 주기
        unscheduleData.addMerchantUid(merchant_uid);//누락되면 빌링키에 관련된 모든 예약정보 일괄취소
        try {
            return iamportClient.unsubscribeSchedule(unscheduleData);
        } catch (IamportResponseException e) {
            throw new CustomException(PAY_UNSCHEDULING_INTERNAL_ERROR);
        } catch (IOException e) {
            throw new CustomException(PAY_UNSCHEDULING_INTERNAL_ERROR);
        }
    }
}
