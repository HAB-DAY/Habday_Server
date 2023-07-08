package com.habday.server.controller;

import com.habday.server.classes.Common;
import com.habday.server.config.S3Uploader;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import com.habday.server.dto.CommonResponse;
import com.habday.server.dto.MemberProfileRequestDto;
import com.habday.server.dto.MemberProfileResponse;
import com.habday.server.dto.req.CreateFundingItemRequestDto;
import com.habday.server.exception.CustomException;
import com.habday.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import static com.habday.server.constants.code.ExceptionCode.NO_MEMBER_ID;
import static com.habday.server.constants.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
public class MemberController extends Common {
    private final MemberService memberService;
    @Autowired
    private S3Uploader s3Uploader;


    @PutMapping("/save/memberProfile/{memberId}")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@PathVariable("memberId") Long memberId, @RequestBody MemberProfileRequestDto request) {
        memberService.updateMemberProfile(memberId, request);
        return MemberProfileResponse.newResponse(VERIFY_MEMBER_PROFILE_SUCCESS);
    }

    @PostMapping("/create/fundingItem/{memberId}")
    public ResponseEntity<CommonResponse> createFundingItem(@PathVariable("memberId") Long memberId, @RequestPart(value="fundingItemImg") MultipartFile fundingItemImg, @RequestPart(value="dto") CreateFundingItemRequestDto request) throws IOException {
        System.out.println("fundingItemImg^^" + fundingItemImg.toString());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        String fundingItemImgUrl = s3Uploader.upload(fundingItemImg, "images");

        FundingItem fundingItem = fundingItemRepository.save(request.toCreateFundingItem(fundingItemImgUrl, request.getFundingName(), request.getFundDetail(), request.getItemPrice(), request.getGoalPrice(), request.getStartDate(), request.getFinishDate(), member));
        String responseDto = "http://13.124.209.40:8080/funding/showFundingContent?itemId=" + fundingItem.getId();
        return CommonResponse.toResponse(CREATE_FUNDING_ITEM_SUCCESS, responseDto);
        //return CreateFundingItemResponseDto.newResponse(CREATE_FUNDING_ITEM_SUCCESS);
    }



}
