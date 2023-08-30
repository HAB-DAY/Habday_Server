package com.habday.server.controller;

import com.habday.server.classes.Common;
import com.habday.server.config.S3Uploader;
import com.habday.server.constants.code.ExceptionCode;
import com.habday.server.constants.state.MemberState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.CommonResponse;
import com.habday.server.dto.req.MemberProfileRequestDto;
import com.habday.server.dto.res.MemberProfileResponse;
import com.habday.server.dto.req.CreateFundingItemRequestDto;
import com.habday.server.exception.CustomException;
import com.habday.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import static com.habday.server.constants.code.ExceptionCode.MEMBER_SUSPENDED;
import static com.habday.server.constants.code.ExceptionCode.NO_MEMBER_ID;
import static com.habday.server.constants.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
public class MemberController extends Common {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final FundingItemRepository fundingItemRepository;
    @Autowired
    private S3Uploader s3Uploader;


    @PutMapping("/save/memberProfile")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@RequestHeader("") String accessToken, @RequestBody MemberProfileRequestDto request) {
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        memberService.updateMemberProfile(memberId, request);
        return MemberProfileResponse.newResponse(VERIFY_MEMBER_PROFILE_SUCCESS);
    }

    @GetMapping("/check/memberProfile")
    public ResponseEntity<CommonResponse> saveMemberProfile(@RequestHeader("") String accessToken) {
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        if(member.getBirthday() == null || member.getBirthday().length() == 0) {
            return CommonResponse.toResponse(ALREADY_MEMBER_EXIST, false);
        }
        return CommonResponse.toResponse(ALREADY_MEMBER_EXIST, true);
    }

    @PostMapping("/create/fundingItem")
    public ResponseEntity<CommonResponse> createFundingItem(@RequestHeader("") String accessToken, @RequestPart(value="fundingItemImg") MultipartFile fundingItemImg, @RequestPart(value="dto") CreateFundingItemRequestDto request) throws IOException {
        Long memberId = jwtService.getMemberIdFromJwt(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        if(fundingItemImg.isEmpty()) {
            throw new CustomException(ExceptionCode.NO_FUNDING_IMG);
        }

        if(member.getStatus().equals(MemberState.SUSPENDED))
            throw new CustomException(MEMBER_SUSPENDED);

        String fundingItemImgUrl = s3Uploader.upload(fundingItemImg, "images");

        FundingItem fundingItem = fundingItemRepository.save(request.toCreateFundingItem(fundingItemImgUrl, request.getFundingName(), request.getFundDetail(), request.getItemPrice(), request.getGoalPrice(), request.getStartDate(), request.getFinishDate(), member));
        String responseDto = "https://habday-web.vercel.app/landing/" + fundingItem.getId();
        return CommonResponse.toResponse(CREATE_FUNDING_ITEM_SUCCESS, responseDto);
    }
}
