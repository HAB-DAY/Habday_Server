package com.habday.server.controller;

import com.habday.server.classes.Common;
import com.habday.server.classes.implemented.HostedList;
import com.habday.server.dto.req.fund.ConfirmationRequest;
import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.CommonResponse;
import com.habday.server.dto.res.DeleteFundingItemResponse;
import com.habday.server.dto.res.UpdateFundingItemResponse;
import com.habday.server.dto.res.fund.*;
import com.habday.server.exception.CustomException;
import com.habday.server.service.FundingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;

import static com.habday.server.constants.code.ExceptionCode.*;
import static com.habday.server.constants.code.SuccessCode.*;

//펀딩 생성, 참여, 삭제, 조회 등 모든 펀딩 로직이 들어가는 부분(추후에 필요할 시 컨트롤러 나눌 예정
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/funding")
public class FundingController extends Common {
    private final FundingService fundingService;
    //private final ParticipatedList participatedList;
    private final HostedList hostedList;

    @PostMapping(value = {"/participateFunding", "/participateFunding/{memberId}"})
    public ResponseEntity<CommonResponse> participateFunding(@Valid @RequestBody ParticipateFundingRequest fundingRequestDto, @PathVariable Optional<Long> memberId){
        log.info("participateFunding error");
        ParticipateFundingResponseDto responseDto = fundingService.participateFunding(fundingRequestDto,memberId.orElseThrow(
                () -> new CustomException(NO_MEMBER_ID)
        ));
        return CommonResponse.toResponse(PARTICIPATE_FUNDING_SUCCESS, responseDto);
    }

    @PostMapping(value = {"/confirm", "confirm/{memberId}"})
    public ResponseEntity<CommonResponse> confirm(@RequestPart(value = "img") MultipartFile img, @RequestPart(value = "dto") ConfirmationRequest request, @PathVariable Optional<Long> memberId){ //, @RequestPart(value = "dto") ConfirmationRequest request, @PathVariable Optional<Long> memberId
        log.info("request: "+  request.getMessage());
        fundingService.confirm(img, request, memberId.orElseThrow(
                () -> new CustomException(NO_MEMBER_ID)
        ));
        return CommonResponse.toResponse(FUNDING_CONFIRMATION_SUCCESS, null);
    }

    @GetMapping("/showFundingContent")
    public ResponseEntity<CommonResponse> showFundingContent(@RequestParam @NotNull(message = "펀딩 상태를 입력해주세요.") Long itemId){
        ShowFundingContentResponseDto responseDto = fundingService.showFundingContent(itemId);
        return CommonResponse.toResponse(SHOW_FUNDING_CONTENT_SUCCESS, responseDto);
    }

    @GetMapping("/itemList/hosted/progress")
    public ResponseEntity<CommonResponse> getHostingList_progress(@RequestParam @NotNull(message = "memberId를 입력해주세요.") Long memberId,
           Long lastItemId){

        GetListResponseDto responseDto = fundingService.getList(hostedList, memberId, "PROGRESS", lastItemId);
        return CommonResponse.toResponse(GET_HOSTING_LIST_SUCCESS, responseDto);
    }

    @GetMapping("/itemList/hosted/finished")
    public ResponseEntity<CommonResponse> getHostingList_finished(@RequestParam @NotNull(message = "memberId를 입력해주세요.") Long memberId,
           Long lastItemId){
        GetListResponseDto responseDto = fundingService.getList(hostedList, memberId, "FINISHED", lastItemId);
        return CommonResponse.toResponse(GET_HOSTING_LIST_SUCCESS, responseDto);
    }

    @GetMapping("/itemList/participated")
    public ResponseEntity<CommonResponse> getParticipatedList(@RequestParam @NotNull(message = "memberId를 입력해주세요.") Long memberId,
           Long lastItemId){
        GetListResponseDto responseDto = fundingService.getParticipateList(memberId,lastItemId);
        return CommonResponse.toResponse(GET_FUNDING_LIST_SUCCESS, responseDto);
    }

//    @GetMapping("/itemList/participated/progress")
//    public ResponseEntity<CommonResponse> getParticipatedList_progress(@RequestParam @NotNull(message = "memberId를 입력해주세요.") Long memberId,
//            Long lastItemId){
//        GetListResponseDto responseDto = fundingService.getList(participatedList, memberId, "PROGRESS", lastItemId);
//        return CommonResponse.toResponse(GET_FUNDING_LIST_SUCCESS, responseDto);
//    }
//
//    @GetMapping("/itemList/participated/finished")
//    public ResponseEntity<CommonResponse> getParticipatedList_finished(@RequestParam @NotNull(message = "memberId를 입력해주세요.") Long memberId,
//            Long lastItemId){
//        GetListResponseDto responseDto = fundingService.getList(participatedList, memberId,"FINISHED", lastItemId);
//        return CommonResponse.toResponse(GET_FUNDING_LIST_SUCCESS, responseDto);
//    }

    // 펀딩 수정
    @PutMapping("/update/{fundingItemId}")
    public ResponseEntity<UpdateFundingItemResponse> updateFundingItem(@PathVariable(value = "fundingItemId") Long fundingItemId, @RequestPart(value="fundingItemImg", required = false) MultipartFile fundingItemImg, @RequestPart(value="fundingItemName", required = false) String fundingItemName, @RequestPart(value = "fundingItemDetail", required = false) String fundingItemDetail) throws IOException {
        fundingService.updateFundingItem(fundingItemId, fundingItemImg, fundingItemName, fundingItemDetail);
        return UpdateFundingItemResponse.newResponse(UPDATE_FUNDING_ITEM_SUCCESS);
    }

    // 펀딩 식제
    @GetMapping("/delete/{fundingItemId}")
    public ResponseEntity<DeleteFundingItemResponse> deleteFundingItem(@PathVariable("fundingItemId") Long fundingItemId) {
        fundingService.deleteFundingItem(fundingItemId);
        return DeleteFundingItemResponse.newResponse(DELETE_FUNDING_ITEM_SUCCESS);
    }
}
