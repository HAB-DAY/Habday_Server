package com.habday.server.controller;

import com.habday.server.constants.CustomException;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.MemberProfileRequestDto;
import com.habday.server.dto.MemberProfileResponse;
import com.habday.server.dto.req.CreateFundingItemRequestDto;
import com.habday.server.dto.res.CreateFundingItemResponse;
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

    @PostMapping("/save/memberProfile")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@RequestBody MemberProfileRequestDto request) {
        memberRepository.save(request.toVerifyMemberProfile(request.getId(), request.getNickName(), request.getBirthday(), request.getProfileContent(), request.getAccount(), request.getAccountName()));
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
