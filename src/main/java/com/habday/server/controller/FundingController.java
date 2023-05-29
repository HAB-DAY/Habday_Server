package com.habday.server.controller;

import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.res.fund.ParticipateFundingResponse;
import com.habday.server.dto.res.fund.ParticipateFundingResponseDto;
import com.habday.server.dto.res.fund.ShowFundingContentResponse;
import com.habday.server.dto.res.fund.ShowFundingContentResponseDto;
import com.habday.server.service.FundingService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

import static com.habday.server.constants.SuccessCode.PARTICIPATE_FUNDING_SUCCESS;
import static com.habday.server.constants.SuccessCode.SHOW_FUNDING_CONTENT_SUCCESS;

//펀딩 생성, 참여, 삭제, 조회 등 모든 펀딩 로직이 들어가는 부분(추후에 필요할 시 컨트롤러 나눌 예정

@RestController
@RequiredArgsConstructor
@RequestMapping("/funding")
public class FundingController {
    private final FundingService fundingService;

    @PostMapping("/participateFunding")
    public ResponseEntity<ParticipateFundingResponse> participateFunding(@Valid @RequestBody ParticipateFundingRequest fundingRequestDto){
        ParticipateFundingResponseDto responseDto = fundingService.participateFunding(fundingRequestDto,1L);
        return ParticipateFundingResponse.newResponse(PARTICIPATE_FUNDING_SUCCESS, responseDto);
    }

    @GetMapping("/showFundingContent")
    public ResponseEntity<ShowFundingContentResponse> showFundingContent(@RequestParam Long itemId){
        ShowFundingContentResponseDto responseDto = fundingService.showFundingContent(itemId);
        return ShowFundingContentResponse.newResponse(SHOW_FUNDING_CONTENT_SUCCESS, responseDto);
    }
}
