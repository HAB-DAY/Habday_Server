package com.habday.server.controller;

import com.google.gson.Gson;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.dto.req.iamport.*;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/verifyIamport")
public class VerifyController {
    private final IamportClient iamportClient;

    // 생성자를 통해 REST API 와 REST API secret 입력
    public VerifyController(){
        this.iamportClient = new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");
    }
    /** 아이앰포트 rest api로 빌링키 획득하기 **/
    @PostMapping("/noneauthpay/getBillingKey")
    public @ResponseBody IamportResponse<BillingCustomer> getBillingKey(@RequestBody NoneAuthPayBillingKeyRequest billingKeyRequest) throws IamportResponseException, IOException {
        BillingCustomerData billingCustomerData = new BillingCustomerData(
                billingKeyRequest.getCustomer_uid(), billingKeyRequest.getCard_number(),
                billingKeyRequest.getExpiry(), billingKeyRequest.getBirth());

        billingCustomerData.setPwd2Digit(billingKeyRequest.getPwd_2digit());
        billingCustomerData.setPg("nice.nictest04m");
        return iamportClient.postBillingCustomer(billingKeyRequest.getCustomer_uid(), billingCustomerData);
    }

    /** 빌링키에 매핑된 결제 데이터 확인하기 **/
    @GetMapping("/noneauthpay/showbillinginfo/{customer_uid}")
    public @ResponseBody IamportResponse<BillingCustomer> showBillingInfo(@PathVariable String customer_uid) throws IamportResponseException, IOException {
        return iamportClient.getBillingCustomer(customer_uid);
    }

    /** 비인증 결제(빌링키) 방식 예약 결제**/
    @PostMapping("/noneauthpay/schedule")
    public @ResponseBody IamportResponse<List<Schedule>> noneAuthPaySchedule(@RequestBody NoneAuthPayScheduleRequestDto scheduleRequestDto) throws IamportResponseException, IOException {
        log.debug("noneAuthPay 진입: 예약 시간: " + scheduleRequestDto.getSchedule_at());
        ScheduleEntry scheduleEntry= new ScheduleEntry(
                scheduleRequestDto.getMerchant_uid(), scheduleRequestDto.getSchedule_at(), scheduleRequestDto.getAmount());
        scheduleEntry.setName(scheduleRequestDto.getName());
        scheduleEntry.setBuyerName(scheduleRequestDto.getBuyer_name());
        scheduleEntry.setBuyerTel(scheduleRequestDto.getBuyer_tel());
        scheduleEntry.setBuyerEmail(scheduleRequestDto.getBuyer_email());

        Gson gson = new Gson();
        log.debug("scheduleEntry: " + gson.toJson(scheduleEntry));

        ScheduleData scheduleData = new ScheduleData(scheduleRequestDto.getCustomer_uid());
        scheduleData.addSchedule(scheduleEntry);
        return iamportClient.subscribeSchedule(scheduleData);
    }

    //todo null체크
    /**예약 취소**/
    @PostMapping("/noneauthpay/unschedule")
    public @ResponseBody IamportResponse<List<Schedule>> noneAuthPayUnschedule(@RequestBody NoneAuthPayUnscheduleRequestDto unscheduleRequestDto) throws IamportResponseException, IOException {
        UnscheduleData unscheduleData = new UnscheduleData(unscheduleRequestDto.getCustomer_uid());
        unscheduleData.addMerchantUid(unscheduleRequestDto.getMerchant_uid());//누락되면 빌링키에 관련된 모든 예약정보 일괄취소
        return iamportClient.unsubscribeSchedule(unscheduleData);
    }


    /**예약결제 확인**/
    @GetMapping("/noneauthpay/showschedules/{customer_uid}")
    public @ResponseBody IamportResponse<ScheduleList> showSchedules(@PathVariable String customer_uid, @RequestParam String schedule_status, @RequestParam int page) throws IamportResponseException, IOException {
        GetScheduleData getScheduleData = new GetScheduleData(1682265600, 1682344800, schedule_status, page, 8);
        return iamportClient.getPaymentSchedule(getScheduleData);
    }



    /** 결제 취소 **/
    @PostMapping("/cancel")
    public @ResponseBody IamportResponse<Payment> cancelItem(@RequestBody CancelPayReqeustDto cancelPayReqeustDto) throws IamportResponseException, IOException {
        //상품번호로 db 검색해서 (imp_uid, amount, cancel_amount) 가져오기
        BigDecimal amount = new BigDecimal(101); //
        BigDecimal cancel_amount = BigDecimal.ZERO;
        BigDecimal cancelableAmount = amount.subtract(cancel_amount);//db에서 가져온 결제정보 - 이전에 취소 처리 된 적 있는 경우

        if (cancelableAmount.compareTo(BigDecimal.ZERO) == 0) {//이미 환불 완료됨
            return new IamportResponse<>();
        }
        CancelData cancelData = new CancelData("merchant_uid", false, cancelPayReqeustDto.getCancel_request_amount());
        cancelData.setChecksum(cancelableAmount);
        cancelData.setReason(cancelPayReqeustDto.getReason());
        log.debug("cancel 완료 직전임");
        return iamportClient.cancelPaymentByImpUid(cancelData);
    }

    /** 웹훅 예약결제 컬백 **/
    @PostMapping("/callback/schedule")
    public @ResponseBody void callbackSchedule(@RequestBody CallbackScheduleRequestDto callbackRequestDto) throws IamportResponseException, IOException {
        //todo 웹훅 ip 검증하기
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(callbackRequestDto.getImp_uid());

        if (callbackRequestDto.getStatus() == ScheduledPayState.fail.getMsg()){
            log.debug("callback 리스폰스 호출/: " + response.getResponse().getFailReason());
        }

        //todo db에 저장하기
    }

    /** 결제 테스트 페이지 **/
    @GetMapping("/payTestView")
    public String payTestView(){
        return "payview.html";
    }
}
