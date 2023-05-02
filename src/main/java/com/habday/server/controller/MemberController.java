package com.habday.server.controller;

import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.MemberProfileRequestDto;
import com.habday.server.dto.MemberProfileResponse;
import com.habday.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.habday.server.constants.SuccessCode.VERIFY_MEMBER_PROFILE_SUCCESS;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/save/memberProfile/{accessToken}")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@Valid @RequestBody MemberProfileRequestDto request) {
        memberRepository.save(request.toVerifyMemberProfile(request.getId(), request.getNickName(), request.getBirthday(), request.getProfileContent(), request.getAccount(), request.getAccountName()));
        return MemberProfileResponse.newResponse(VERIFY_MEMBER_PROFILE_SUCCESS);
    }


}
