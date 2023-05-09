package com.habday.server.controller;

import com.habday.server.constants.CustomException;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.req.member.MemberProfileRequestDto;
import com.habday.server.dto.res.member.MemberProfileResponse;
import com.habday.server.dto.req.fund.CreateFundingItemRequestDto;
import com.habday.server.dto.res.fund.CreateFundingItemResponse;
import com.habday.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static com.habday.server.constants.ExceptionCode.NO_MEMBER_ID;
import static com.habday.server.constants.SuccessCode.CREATE_FUNDING_ITEM_SUCCESS;
import static com.habday.server.constants.SuccessCode.VERIFY_MEMBER_PROFILE_SUCCESS;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    private final FundingItemRepository fundingItemRepository;

    private final MemberService memberService;


    @PutMapping("/save/memberProfile/{memberId}")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@PathVariable("memberId") Long memberId, @RequestBody MemberProfileRequestDto request) {
        memberService.updateMemberProfile(memberId, request);
        return MemberProfileResponse.newResponse(VERIFY_MEMBER_PROFILE_SUCCESS);
    }

    @PostMapping("/create/fundingItem/{memberId}")
    public ResponseEntity<CreateFundingItemResponse> createFundingItem(@PathVariable("memberId") Long memberId, @RequestBody CreateFundingItemRequestDto request) {

        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        fundingItemRepository.save(request.toCreateFundingItem(request.getFundingItemImg(), request.getFundingName(), request.getFundDetail(), request.getItemPrice(), request.getTotalPrice(), request.getGoalPrice(), request.getStartDate(), request.getFinishDate(), member));
        return CreateFundingItemResponse.newResponse(CREATE_FUNDING_ITEM_SUCCESS);
    }



}
