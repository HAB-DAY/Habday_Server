package com.habday.server.controller;

import com.habday.server.classes.Common;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.dto.req.iamport.CallbackScheduleRequestDto;
import com.habday.server.exception.CustomException;
import com.habday.server.service.FundingCloseService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.habday.server.constants.ExceptionCode.NO_FUNDING_ITEM_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fundingClose")
public class FundingCloseController extends Common {
    private final FundingCloseService fundingCloseService;

    @GetMapping("/checkSuccess/{fundingItemId}")
    public void checkFundingResult(@PathVariable Long fundingItemId) {
        fundingCloseService.checkFundingFinishDate(fundingItemId);
    }

    /** 웹훅 예약결제 컬백 **/
    @PostMapping("/callback/schedule")
    public @ResponseBody void callbackSchedule(@RequestBody CallbackScheduleRequestDto callbackRequestDto, HttpServletRequest request) throws IamportResponseException, IOException {
        fundingCloseService.callbackSchedule(callbackRequestDto, request);
    }
}
