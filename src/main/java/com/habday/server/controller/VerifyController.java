package com.habday.server.controller;

import com.habday.server.dto.req.iamport.CallbackScheduleRequestDto;
import com.habday.server.dto.req.iamport.NoneAuthPayBillingKeyRequest;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.BillingCustomerData;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    /** 인증결제 방식으로 imp_uid로 카드사 결제 **/
    @PostMapping("/authpay/{imp_uid}")
    public @ResponseBody IamportResponse<Payment> authPay(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        log.info("payByImpUidAuth 진입: " +imp_uid);
        return iamportClient.paymentByImpUid(imp_uid);
    }

    /** 결제 테스트 페이지 **/
    @GetMapping("/payTestView")
    public String payTestView(){
        return "payview.html";
    }

    /** 비인증 결제(빌링키) 방식 예약 결제 **/
    @PostMapping("/noneauthpay/schedule")
    public @ResponseBody IamportResponse<List<Schedule>> noneAuthPaySchedule(@RequestBody NoneAuthPayScheduleRequestDto scheduleRequestDto) throws IamportResponseException, IOException {
        log.info("noneAuthPay 진입: request: " + scheduleRequestDto.printRequest());
        ScheduleEntry scheduleEntry= new ScheduleEntry(
                scheduleRequestDto.getMerchant_uid(), scheduleRequestDto.getSchedule_at(), scheduleRequestDto.getAmount());
        scheduleEntry.setName(scheduleRequestDto.getName());
        scheduleEntry.setBuyerName(scheduleRequestDto.getBuyer_name());
        scheduleEntry.setBuyerTel(scheduleRequestDto.getBuyer_tel());
        scheduleEntry.setBuyerEmail(scheduleRequestDto.getBuyer_email());

        ScheduleData scheduleData = new ScheduleData(scheduleRequestDto.getCustomer_uid());
        scheduleData.addSchedule(scheduleEntry);
        return iamportClient.subscribeSchedule(scheduleData);
    }

    /** 웹훅 예약결제 컬백 **/
    @PostMapping("/callback/schedule")
    public @ResponseBody IamportResponse<Payment> callbackSchedule(@RequestBody CallbackScheduleRequestDto callbackRequestDto) throws IamportResponseException, IOException {
        //todo 웹훅 ip 검증하기
        log.info("callback 호출: " + callbackRequestDto.printRequest());
        //todo db에 저장하기
        return null;
    }

    /** 빌링키에 매핑된 결제 데이터 확인하기 **/
    @GetMapping("/noneauthpay/showbillinginfo/{customer_uid}")
    public @ResponseBody IamportResponse<BillingCustomer> showBillingInfo(@PathVariable String customer_uid) throws IamportResponseException, IOException {
        //iamportClient.getPaymentSchedule();
        log.info("showUserOrders" + iamportClient.getBillingCustomer(customer_uid).toString());
        return iamportClient.getBillingCustomer(customer_uid);
    }

    /** 아이앰포트 rest api로 빌링키 획득하기 **/
    @PostMapping("/noneauthpay/getBillingKey")
    public @ResponseBody IamportResponse<BillingCustomer> getBillingKey(@RequestBody NoneAuthPayBillingKeyRequest billingKeyRequest) throws IamportResponseException, IOException {
        log.info("getBillingKey start");
        BillingCustomerData billingCustomerData = new BillingCustomerData(
                billingKeyRequest.getCustomer_uid(), billingKeyRequest.getCard_number(),
                billingKeyRequest.getExpiry(), billingKeyRequest.getBirth());

        billingCustomerData.setPwd2Digit(billingKeyRequest.getPwd_2digit());
        billingCustomerData.setPg("nice.nictest04m");
        return iamportClient.postBillingCustomer(billingKeyRequest.getCustomer_uid(), billingCustomerData);
    }

    /**예약결제 취소**/
}
