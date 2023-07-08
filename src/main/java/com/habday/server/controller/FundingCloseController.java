package com.habday.server.controller;

import com.habday.server.classes.Common;
import com.habday.server.config.email.EmailMessage;
import com.habday.server.config.email.EmailService;
import com.habday.server.constants.CmnConst;
import com.habday.server.dto.req.iamport.CallbackScheduleRequestDto;
import com.habday.server.service.FundingCloseService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fundingClose")
public class FundingCloseController extends Common {
    private final FundingCloseService fundingCloseService;
    private final EmailService emailService;

    @GetMapping("/checkSuccess/{fundingItemId}")
    public void checkFundingResult(@PathVariable Long fundingItemId) {
        fundingCloseService.checkFundingFinishDate(fundingItemId);
    }

    /** 웹훅 예약결제 컬백 **/
    @PostMapping("/callback/schedule")
    public @ResponseBody void callbackSchedule(@RequestBody CallbackScheduleRequestDto callbackRequestDto, HttpServletRequest request) throws IamportResponseException, IOException {
        fundingCloseService.callbackSchedule(callbackRequestDto, request);
    }

    @GetMapping("/test")
    public void test(){
        //String email = fundingMember.getMember().getEmail();
        String[] receiveList = {"yeonj630@gmail.com", "yeonj630@sookmyung.ac.kr"};
        EmailMessage emailMessage = EmailMessage.builder()
                .to(receiveList)
                .subject("HABDAY" + "펀딩 성공 알림" )
                .message("펀딩이 성공했습니다. \n" +
                        "00시 " + CmnConst.paymentDelayMin + "분에 결제 처리될 예정입니다.")
                .build();
        emailService.sendEmail(emailMessage);
    }
}