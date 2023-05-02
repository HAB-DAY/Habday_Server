package com.habday.server.controller;

import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.MemberProfileRequestDto;
import com.habday.server.dto.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static com.habday.server.constants.SuccessCode.VERIFY_MEMBER_PROFILE_SUCCESS;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @PostMapping("/save/memberProfile")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@RequestBody MemberProfileRequestDto request) {
        memberRepository.save(request.toVerifyMemberProfile(request.getId(), request.getNickName(), request.getBirthday(), request.getProfileContent(), request.getAccount(), request.getAccountName()));
        return MemberProfileResponse.newResponse(VERIFY_MEMBER_PROFILE_SUCCESS);
    }


}
