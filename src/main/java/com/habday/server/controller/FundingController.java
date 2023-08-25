package com.habday.server.controller;

import com.habday.server.classes.Common;
import com.habday.server.classes.implemented.HostedList;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.req.fund.ConfirmationRequest;
import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.CommonResponse;
import com.habday.server.dto.req.iamport.NoneAuthPayUnscheduleRequestDto;
import com.habday.server.dto.res.DeleteFundingItemResponse;
import com.habday.server.dto.res.ShowFundingDdayResponseDto;
import com.habday.server.dto.res.UpdateFundingItemResponse;
import com.habday.server.dto.res.fund.*;
import com.habday.server.dto.res.iamport.UnscheduleResponseDto;
import com.habday.server.exception.CustomException;
import com.habday.server.service.FundingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    private final FundingItemRepository fundingItemRepository;
    private final MemberRepository memberRepository;
    private final FundingService fundingService;
    //private final ParticipatedList participatedList;
    private final HostedList hostedList;

    @PostMapping(value = {"/participateFunding"})
    public ResponseEntity<CommonResponse> participateFunding(@RequestHeader("") String accessToken, @Valid @RequestBody ParticipateFundingRequest fundingRequestDto){
        log.info("participateFunding error");
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        ParticipateFundingResponseDto responseDto = fundingService.participateFunding(fundingRequestDto,memberId);
        return CommonResponse.toResponse(PARTICIPATE_FUNDING_SUCCESS, responseDto);
    }
    /*
    * 결제 시
    * paymentId:2 - memberId1이랑 매핑됨 (사용자는 자신의 빌링키를 알면 안됨)
    * 다른 사용자가 1번으로 결제하지 못하게
    * - accessToken에서 가져온 memberId와 paymentId의 memberId가 일치하는지
    * */

    @PostMapping(value = {"/confirm"})
    public ResponseEntity<CommonResponse> confirm(@RequestHeader("") String accessToken, @RequestPart(value = "img") MultipartFile img,
        @Valid @RequestPart(value = "dto") ConfirmationRequest request, @RequestParam @NotNull(message = "펀딩 아이템 아이디는 필수 입니다.") Long fundingItemId){ //, @RequestPart(value = "dto") ConfirmationRequest request, @PathVariable Optional<Long> memberId
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        fundingService.confirm(img, request, fundingItemId,memberId);
        return CommonResponse.toResponse(FUNDING_CONFIRMATION_SUCCESS, null);
    }

    @GetMapping(value = {"/showConfirmation", "/showConfirmation/{fundingItemId}"})
    public ResponseEntity<CommonResponse> showConfirmation(@PathVariable Optional<Long> fundingItemId){

        ShowConfirmationResponseDto response = fundingService.showConfirmation(fundingItemId.orElseThrow(() ->
                new CustomException(NO_FUNDING_ITEM_ID)));
        return CommonResponse.toResponse(SHOW_FUNDING_CONFIRM_SUCCESS, response);
    }

    @GetMapping("/showFundingContent")
    //public ResponseEntity<CommonResponse> showFundingContent(@RequestParam @NotNull(message = "펀딩 상태를 입력해주세요.") Long itemId){
    public ResponseEntity<CommonResponse> showFundingContent(@RequestParam @NotNull(message = "펀딩 상태를 입력해주세요.") Long itemId) {
        ShowFundingContentResponseDto responseDto = fundingService.showFundingContent(itemId);
        return CommonResponse.toResponse(SHOW_FUNDING_CONTENT_SUCCESS, responseDto);
    }

    @GetMapping("/itemList/hosted/progress")
    public ResponseEntity<CommonResponse> getHostingList_progress(@RequestHeader("") String accessToken,
           Long lastItemId){
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        GetListResponseDto responseDto = fundingService.getList(hostedList, memberId, "PROGRESS", lastItemId);
        return CommonResponse.toResponse(GET_HOSTING_LIST_SUCCESS, responseDto);
    }

    @GetMapping("/itemList/hosted/finished")
    public ResponseEntity<CommonResponse> getHostingList_finished(@RequestHeader("") String accessToken,
           Long lastItemId){
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        GetListResponseDto responseDto = fundingService.getList(hostedList, memberId, "FINISHED", lastItemId);
        return CommonResponse.toResponse(GET_HOSTING_LIST_SUCCESS, responseDto);
    }

    @GetMapping("/itemList/participated")
    public ResponseEntity<CommonResponse> getParticipatedList(@RequestHeader("") String accessToken,
           Long lastItemId){
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
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
    public ResponseEntity<UpdateFundingItemResponse> updateFundingItem(@RequestHeader("") String accessToken, @PathVariable("fundingItemId") Long fundingItemId, @RequestPart(value="fundingItemImg", required = false) MultipartFile fundingItemImg, @RequestPart(value="fundingItemName", required = false) String fundingItemName, @RequestPart(value = "fundingItemDetail", required = false) String fundingItemDetail) throws IOException {
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        System.out.println("updateFundingItem^^ memberId" + memberId);
        fundingService.updateFundingItem(fundingItemId, fundingItemImg, fundingItemName, fundingItemDetail);
        System.out.println("updateFundingItem^^ finish");
        return UpdateFundingItemResponse.newResponse(UPDATE_FUNDING_ITEM_SUCCESS);
        //return CommonResponse.toResponse(UPDATE_FUNDING_ITEM_SUCCESS, null);
    }

    // 펀딩 식제
    @DeleteMapping ("/delete/{fundingItemId}")
    public ResponseEntity<CommonResponse> deleteFundingItem(@RequestHeader("") String accessToken, @PathVariable("fundingItemId") Long fundingItemId) {
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        fundingService.deleteFundingItem(fundingItemId);
        return CommonResponse.toResponse(DELETE_FUNDING_ITEM_SUCCESS, null);
    }

    @PostMapping("/cancel")
    public ResponseEntity<CommonResponse> cancel(@RequestHeader("") String accessToken, @Valid @RequestBody NoneAuthPayUnscheduleRequestDto request){
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);//혹시 이상한 사람이 memberId만 가져와서 결제 취소할까봐
        UnscheduleResponseDto responseDto = fundingService.cancel(memberId, request);
        return CommonResponse.toResponse(PAY_UNSCHEDULING_SUCCESS, responseDto);
    }

    // 사용자 이름, 남은 생일 일수
    @GetMapping("/showBirthdayLeftDay")
    public ResponseEntity<CommonResponse> showFundingDday(@RequestHeader("") String accessToken) {
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        Long leftday = fundingService.getBirthdayLeft(member);
        ShowFundingDdayResponseDto responseDto = new ShowFundingDdayResponseDto(member.getName(), leftday, member.getBirthday());

        return CommonResponse.toResponse(SHOW_NAME_BIRTHDAY_LEFT, responseDto);

    }
}
