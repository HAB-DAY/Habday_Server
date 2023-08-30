package com.habday.server.controller;

import com.habday.server.classes.Common;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.req.iamport.*;
import com.habday.server.dto.CommonResponse;
import com.habday.server.dto.res.iamport.*;
import com.habday.server.exception.CustomException;
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

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static com.habday.server.constants.code.ExceptionCode.NO_MEMBER_ID;
import static com.habday.server.constants.code.ExceptionCode.NO_PAYMENT_EXIST;
import static com.habday.server.constants.code.SuccessCode.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/verifyIamport")
public class PayController extends Common {
    // 생성자를 통해 REST API 와 REST API secret 입력
    private final IamportClient iamportClient =
            new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");;
    private final PayService payService;

    /** 아이앰포트 rest api로 빌링키 획득하기(카드 등록) **/
    @PostMapping(value = {"/noneauthpay/getBillingKey"})
    public @ResponseBody ResponseEntity<CommonResponse> getBillingKey(@RequestHeader("") String accessToken, @Valid @RequestBody NoneAuthPayBillingKeyRequestDto billingKeyRequest){
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        GetBillingKeyResponseDto responseDto = payService.getBillingKey(billingKeyRequest, memberId);
        return CommonResponse.toResponse(CREATE_BILLING_KEY_SUCCESS, responseDto);
    }

    /**등록된 카드 삭제**/
    @DeleteMapping(value = {"/noneauthpay/delete/{paymentId}", "/noneauthpay/delete"})
    public @ResponseBody ResponseEntity<CommonResponse> deleteBillingKey(@RequestHeader("") String accessToken, @PathVariable Optional<Long> paymentId){//@RequestBody DeleteBillingKeyRequestDto request
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        DeleteBillingKeyResponseDto responseDto = payService.deleteBillingKey(paymentId.orElseThrow(
                () -> new CustomException(NO_PAYMENT_EXIST)
        ));

        return CommonResponse.toResponse(DELETING_BILLING_KEY_SUCCESS, responseDto);
    }

    /** 저장된 결제정보 가져오기**/
    @GetMapping(value = {"/noneauthpay/getPaymentLists"}) //사용자 정보를 jwt에서 가져와서 사용자가 갖고 있는 결제 정보 반환하기
    public @ResponseBody ResponseEntity<CommonResponse> getPaymentLists(@RequestHeader("") String accessToken){
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        GetPaymentListsResponseDto responseDto = payService.getPaymentLists(memberId);
        return CommonResponse.toResponse(GET_PAYMENT_LISTS_SUCCESS, responseDto);
    }

    //todo null체크
    /**예약 취소**/
//    @PostMapping("/noneauthpay/unschedule")
//    public @ResponseBody ResponseEntity<CommonResponse> noneAuthPayUnschedule(@RequestBody NoneAuthPayUnscheduleRequestDto unscheduleRequestDto){//, @PathVariable Optional<Long> memberId
////        memberId.orElseThrow(
////                () -> new CustomException(NO_MEMBER_ID)
////        );
//        log.info("예약 취소 시작");
//        UnscheduleResponseDto response = payService.noneAuthPayUnschedule(unscheduleRequestDto);
//        return CommonResponse.toResponse(PAY_UNSCHEDULING_SUCCESS, response);
//    }


    /** 예약결제 확인 **/
    @GetMapping("/noneauthpay/showschedules")
    public @ResponseBody IamportResponse<ScheduleList> showSchedules(@RequestBody ShowSchedulesRequestDto showSchedulesRequestDto){
        return payService.showSchedules(showSchedulesRequestDto);
    }


    /** 결제 취소 **/
//    @PostMapping("/cancel")
//    public @ResponseBody IamportResponse<Payment> cancelItem(@RequestBody CancelPayRequestDto cancelPayRequestDto) throws IamportResponseException, IOException {
//        //상품번호로 db 검색해서 (imp_uid, amount, cancel_amount) 가져오기
//        BigDecimal amount = new BigDecimal(101); //
//        BigDecimal cancel_amount = BigDecimal.ZERO;
//        BigDecimal cancelableAmount = amount.subtract(cancel_amount);//db에서 가져온 결제정보 - 이전에 취소 처리 된 적 있는 경우
//
//        if (cancelableAmount.compareTo(BigDecimal.ZERO) == 0) {//이미 환불 완료됨
//            return new IamportResponse<>();
//        }
//        CancelData cancelData = new CancelData("merchant_uid", false, cancelPayRequestDto.getCancel_request_amount());
//        cancelData.setChecksum(cancelableAmount);
//        cancelData.setReason(cancelPayRequestDto.getReason());
//        log.info("cancel 완료 직전임");
//        return iamportClient.cancelPaymentByImpUid(cancelData);
//    }

}
