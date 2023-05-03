package com.habday.server.controller;

import com.google.gson.Gson;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.req.iamport.*;
import com.habday.server.dto.res.iamport.GetBillingKeyResponse;
import com.habday.server.dto.res.iamport.GetBillingKeyResponseDto;
import com.habday.server.dto.res.iamport.GetPaymentListsResponse;
import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto;
import com.habday.server.service.VerifyIamportService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.habday.server.constants.SuccessCode.CREATE_BILLING_KEY_SUCCESS;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/verifyIamport")
public class VerifyIamportController {
    // 생성자를 통해 REST API 와 REST API secret 입력
    private final IamportClient iamportClient =
            new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");;
    private final VerifyIamportService verifyIamportService;

    //todo 예외 throw 없애기
    /** 아이앰포트 rest api로 빌링키 획득하기 **/
    @PostMapping("/noneauthpay/getBillingKey")
    public @ResponseBody ResponseEntity<GetBillingKeyResponse> getBillingKey(@RequestBody NoneAuthPayBillingKeyRequest billingKeyRequest) throws IamportResponseException, IOException {
        GetBillingKeyResponseDto responseDto = verifyIamportService.getBillingKey(billingKeyRequest, 1L);
        return GetBillingKeyResponse.toResponse(CREATE_BILLING_KEY_SUCCESS, responseDto);
    }

    /** 저장된 결제정보 가져오기**/
    @GetMapping("/noneauthpay/getPaymentLists") //사용자 정보를 jwt에서 가져와서 사용자가 갖고 있는 결제 정보 반환하기
    public @ResponseBody ResponseEntity<GetPaymentListsResponse> getPaymentLists(){
        GetPaymentListsResponseDto responseDto = verifyIamportService.getPaymentLists(1L);
        return GetPaymentListsResponse.newResponse(SuccessCode.GET_PAYMENT_LISTS_SUCCESS, responseDto);
    }


    /** 빌링키에 매핑된 결제 데이터 확인하기 **/
    @GetMapping("/noneauthpay/showbillinginfo/{customer_uid}")
    public @ResponseBody IamportResponse<BillingCustomer> showBillingInfo(@PathVariable String customer_uid) throws IamportResponseException, IOException {
        return iamportClient.getBillingCustomer(customer_uid);
    }

    /** 비인증 결제(빌링키) 방식 예약 결제**/
    @PostMapping("/noneauthpay/schedule")
    public @ResponseBody void noneAuthPaySchedule(@RequestBody NoneAuthPayScheduleRequestDto scheduleRequestDto) throws IamportResponseException, IOException {
        verifyIamportService.noneAuthPaySchedule(scheduleRequestDto);
        //return iamportClient.subscribeSchedule(scheduleData);
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
    public @ResponseBody void callbackSchedule(@RequestBody CallbackScheduleRequestDto callbackRequestDto, HttpServletRequest request) throws IamportResponseException, IOException {
        String clientIp = getIp(request);
        String[] ipLists = {"52.78.100.19", "52.78.48.223", "52.78.5.241"};

        for(String ip : ipLists){
            if(clientIp == ip)
                break;
            else
                throw new RuntimeException("비정상적인 사용자로부터의 접근입니다.");
        }

        IamportResponse<Payment> response = iamportClient.paymentByImpUid(callbackRequestDto.getImp_uid());
        //order db에 저장된 요청 금액 == response로 받은 imp_uid에서 보낸 금액이 일치하는지 확인-> db에 저장
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


    private String getIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        log.info(">>>> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info(">>>> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP"); // 웹로직
            log.info(">>>> WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info(">>>> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info(">>>> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        log.info(">>>> Result : IP Address : "+ip);

        return ip;

    }
}
