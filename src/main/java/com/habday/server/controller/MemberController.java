package com.habday.server.controller;

import com.habday.server.config.S3Uploader;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.MemberProfileRequestDto;
import com.habday.server.dto.MemberProfileResponse;
import com.habday.server.dto.req.CreateFundingItemRequestDto;
import com.habday.server.dto.res.CreateFundingItemResponse;
import com.habday.server.exception.CustomException;
import com.habday.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import static com.habday.server.constants.ExceptionCode.NO_MEMBER_ID;
import static com.habday.server.constants.SuccessCode.CREATE_FUNDING_ITEM_SUCCESS;
import static com.habday.server.constants.SuccessCode.VERIFY_MEMBER_PROFILE_SUCCESS;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final MemberService memberService;
    @Autowired
    private S3Uploader s3Uploader;


    @PutMapping("/save/memberProfile/{memberId}")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@PathVariable("memberId") Long memberId, @RequestBody MemberProfileRequestDto request) {
        memberService.updateMemberProfile(memberId, request);
        return MemberProfileResponse.newResponse(VERIFY_MEMBER_PROFILE_SUCCESS);
    }

    @PostMapping("/create/fundingItem/{memberId}")
    public ResponseEntity<CreateFundingItemResponse> createFundingItem(@PathVariable("memberId") Long memberId, @RequestPart(value="fundingItemImg") MultipartFile fundingItemImg, @RequestPart(value="dto") CreateFundingItemRequestDto request) throws IOException {
        System.out.println("fundingItemImg^^" + fundingItemImg.toString());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        String fundingItemImgUrl = s3Uploader.upload(fundingItemImg, "images");

        fundingItemRepository.save(request.toCreateFundingItem(fundingItemImgUrl, request.getFundingName(), request.getFundDetail(), request.getItemPrice(), request.getTotalPrice(), request.getGoalPrice(), request.getStartDate(), request.getFinishDate(), member));
        return CreateFundingItemResponse.newResponse(CREATE_FUNDING_ITEM_SUCCESS);
    }



}
