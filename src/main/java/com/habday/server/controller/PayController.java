package com.habday.server.controller;

import com.habday.server.constants.ScheduledPayState;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.dto.req.iamport.*;
import com.habday.server.dto.res.iamport.*;
import com.habday.server.exception.CustomExceptionWithMessage;
import com.habday.server.service.PayService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;

import static com.habday.server.constants.ExceptionCode.WEBHOOK_FAIL;
import static com.habday.server.constants.ScheduledPayState.fail;
import static com.habday.server.constants.SuccessCode.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/verifyIamport")
public class PayController {
    // 생성자를 통해 REST API 와 REST API secret 입력
    private final IamportClient iamportClient =
            new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");;
    private final PayService payService;
    private final FundingMemberRepository fundingMemberRepository;

    /** 아이앰포트 rest api로 빌링키 획득하기 **/
    @PostMapping("/noneauthpay/getBillingKey")
    public @ResponseBody ResponseEntity<GetBillingKeyResponse> getBillingKey(@Valid @RequestBody NoneAuthPayBillingKeyRequest billingKeyRequest){
        GetBillingKeyResponseDto responseDto = payService.getBillingKey(billingKeyRequest, 1L);
        return GetBillingKeyResponse.toResponse(CREATE_BILLING_KEY_SUCCESS, responseDto);
    }

    /** 저장된 결제정보 가져오기**/
    @GetMapping("/noneauthpay/getPaymentLists") //사용자 정보를 jwt에서 가져와서 사용자가 갖고 있는 결제 정보 반환하기
    public @ResponseBody ResponseEntity<GetPaymentListsResponse> getPaymentLists(){
        GetPaymentListsResponseDto responseDto = payService.getPaymentLists(1L);
        return GetPaymentListsResponse.newResponse(GET_PAYMENT_LISTS_SUCCESS, responseDto);
    }


    /** 빌링키에 매핑된 결제 데이터 확인하기 **/
    /*@GetMapping("/noneauthpay/showbillinginfo/{customer_uid}")
    public @ResponseBody IamportResponse<BillingCustomer> showBillingInfo(@PathVariable String customer_uid) throws IamportResponseException, IOException {
        return iamportClient.getBillingCustomer(customer_uid);
    }*/

    /** 비인증 결제(빌링키) 방식 예약 결제(FundingController에서 연결 예정)**/
    /*@PostMapping("/noneauthpay/schedule")
    public @ResponseBody void noneAuthPaySchedule(@RequestBody NoneAuthPayScheduleRequestDto scheduleRequestDto) throws IamportResponseException, IOException {
        verifyIamportService.noneAuthPaySchedule(scheduleRequestDto);
        //return iamportClient.subscribeSchedule(scheduleData);
    }*/

    //todo null체크
    /**예약 취소**/
    @PostMapping("/noneauthpay/unschedule")
    public @ResponseBody ResponseEntity<UnscheduleResponse> noneAuthPayUnschedule(@RequestBody NoneAuthPayUnscheduleRequestDto unscheduleRequestDto){
        UnscheduleResponseDto response = payService.noneAuthPayUnschedule(unscheduleRequestDto, 1L);
        return UnscheduleResponse.newResponse(PAY_UNSCHEDULING_SUCCESS, response);
    }


    /** 예약결제 확인 **/
    @GetMapping("/noneauthpay/showschedules")
    public @ResponseBody IamportResponse<ScheduleList> showSchedules(@RequestBody ShowSchedulesRequestDto showSchedulesRequestDto){
        return payService.showSchedules(showSchedulesRequestDto);
    }


    /** 결제 취소 **/
    @PostMapping("/cancel")
    public @ResponseBody IamportResponse<Payment> cancelItem(@RequestBody CancelPayRequestDto cancelPayRequestDto) throws IamportResponseException, IOException {
        //상품번호로 db 검색해서 (imp_uid, amount, cancel_amount) 가져오기
        BigDecimal amount = new BigDecimal(101); //
        BigDecimal cancel_amount = BigDecimal.ZERO;
        BigDecimal cancelableAmount = amount.subtract(cancel_amount);//db에서 가져온 결제정보 - 이전에 취소 처리 된 적 있는 경우

        if (cancelableAmount.compareTo(BigDecimal.ZERO) == 0) {//이미 환불 완료됨
            return new IamportResponse<>();
        }
        CancelData cancelData = new CancelData("merchant_uid", false, cancelPayRequestDto.getCancel_request_amount());
        cancelData.setChecksum(cancelableAmount);
        cancelData.setReason(cancelPayRequestDto.getReason());
        log.debug("cancel 완료 직전임");
        return iamportClient.cancelPaymentByImpUid(cancelData);
    }

    /** 웹훅 예약결제 컬백 **/
    @PostMapping("/callback/schedule")
    public @ResponseBody void callbackSchedule(@RequestBody CallbackScheduleRequestDto callbackRequestDto, HttpServletRequest request) throws IamportResponseException, IOException {
        payService.callbackSchedule(callbackRequestDto, request);
    }


    /** 결제 테스트 페이지 **/
    @GetMapping("/payTestView")
    public String payTestView(){
        return "payview.html";
    }
}
