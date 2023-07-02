package com.habday.server.service;

import com.habday.server.classes.Calculation;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.Payment;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.dto.req.iamport.NoneAuthPayBillingKeyRequestDto;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.habday.server.dto.req.iamport.ShowSchedulesRequestDto;
import com.habday.server.exception.CustomException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import com.siot.IamportRestClient.response.ScheduleList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static com.habday.server.constants.ExceptionCode.*;
import static com.habday.server.constants.ExceptionCode.PAY_UNSCHEDULING_INTERNAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportService {
    private final PaymentRepository paymentRepository;
    private final Calculation calculation;
    private final IamportClient iamportClient =
            new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");

    public IamportResponse<BillingCustomer> getBillingKeyFromIamport(NoneAuthPayBillingKeyRequestDto billingKeyRequest, String customer_uid){
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

    public IamportResponse<BillingCustomer> deleteBillingKeyFromIamport(String customerUid, String reason, String extra){
        try {
            return iamportClient.deleteBillingCustomer(customerUid, reason, extra);
        } catch (IOException e) {
            throw new CustomException(DELETING_BILLING_KEY_FAIL_INTERNAL_ERROR);
        } catch (IamportResponseException e) {
            throw new CustomException(DELETING_BILLING_KEY_FAIL_INTERNAL_ERROR);
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

    //예약내역 확인
    public IamportResponse<ScheduleList> showSchedulesFromIamport(ShowSchedulesRequestDto requestDto){
        Long schedule_from = calculation.getUnixTimeStamp(requestDto.getS_year(), requestDto.getS_month(), requestDto.getS_date());
        Long schedule_to = calculation.getUnixTimeStamp(requestDto.getE_year(), requestDto.getE_month(), requestDto.getE_date());
        log.debug("IamportService.showSchedules: " + schedule_from + " "  + schedule_to);
        GetScheduleData getScheduleData = new GetScheduleData(schedule_from.intValue(), schedule_to.intValue(), requestDto.getSchedule_status(), requestDto.getPage(), 8);
        try {
            return iamportClient.getPaymentSchedule(getScheduleData);
        } catch (IamportResponseException e) {
            throw new CustomException(SHOW_SCHEDULE_INTERNAL_ERROR);
        } catch (IOException e) {
            throw new CustomException(SHOW_SCHEDULE_INTERNAL_ERROR);
        }
    }

    public IamportResponse<com.siot.IamportRestClient.response.Payment> paymentByImpUid(String imp_uid){
        try {
            return iamportClient.paymentByImpUid(imp_uid);
        } catch (IamportResponseException e) {
            throw new CustomException(GET_PAY_INFO_INTERNAL_ERROR);
        } catch (IOException e) {
            throw new CustomException(GET_PAY_INFO_INTERNAL_ERROR);
        }
    }

    //모든 결제 내역 확인
}
