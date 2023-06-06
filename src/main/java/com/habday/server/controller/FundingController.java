package com.habday.server.controller;

import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.res.fund.*;
import com.habday.server.service.FundingService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

import static com.habday.server.constants.SuccessCode.*;

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

    @GetMapping("/itemList/host")
    public ResponseEntity<GetHostingListResponse> getHostItemList(@RequestParam Long memberId, @RequestParam String status, @RequestParam Integer page){
        GetHostingListResponseDto responseDto = fundingService.getHostItemList(memberId, status, page);
        return GetHostingListResponse.newResponse(GET_FUNDING_LIST_SUCCESS, responseDto);
    }

    //필요한 데이터: memberId/state/날짜/페이지
    @GetMapping("/itemList/participate")
    public ResponseEntity<GetParticipatedListResponse> getParticipatedList(@RequestParam Long memberId, @RequestParam String status,
        @RequestParam Integer page){
        GetParticipatedListResponseDto responseDto = fundingService.getParticipatedList(memberId, status, page);
        return GetParticipatedListResponse.newResponse(GET_FUNDING_LIST_SUCCESS, responseDto);
    }
}
