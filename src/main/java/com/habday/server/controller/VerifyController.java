package com.habday.server.controller;

import com.habday.server.dto.req.iamport.CallbackScheduleRequestDto;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
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

    /** 프론트에서 받은 PG사 결괏값을 통해 아임포트 토큰 발행 **/
    @PostMapping("/authpay/{imp_uid}")// todo 데이터베이스에 결제 정보 저장하기
    public @ResponseBody IamportResponse<Payment> authPay(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        log.info("payByImpUidAuth 진입: " +imp_uid);
        return iamportClient.paymentByImpUid(imp_uid);
    }

    //http://localhost:9000/verifyIamport/payTestView
    @GetMapping("/payTestView")
    public String payTestView(){
        return "payview.html";
    }

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

    @PostMapping("/callback/schedule")
    public @ResponseBody IamportResponse<Payment> callbackSchedule(@RequestBody CallbackScheduleRequestDto callbackRequestDto) throws IamportResponseException, IOException {
        //todo 웹훅 ip 검증하기
        log.info("callback 호출: " + callbackRequestDto.printRequest());
        //String result = iamportClient.paymentByImpUid(callbackRequestDto.getImp_uid()).;
        return null;
    }

    @GetMapping("/showUserOrders/{customer_uid}")
    public @ResponseBody IamportResponse<BillingCustomer> showUserOrders(@PathVariable String customer_uid) throws IamportResponseException, IOException {
        //iamportClient.getPaymentSchedule();
        log.info("showUserOrders" + iamportClient.getBillingCustomer(customer_uid).toString());
        return iamportClient.getBillingCustomer(customer_uid);
    }
}
