package com.habday.server.service;

import com.habday.server.classes.Common;
import com.habday.server.domain.member.Member;
import com.habday.server.dto.MemberProfileRequestDto;
import com.habday.server.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.habday.server.constants.code.ExceptionCode.NO_MEMBER_ID;

@Service
@RequiredArgsConstructor
public class MemberService extends Common {

    @Transactional
    public void updateMemberProfile(Long memberId, MemberProfileRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        member.update(requestDto.getNickName(), requestDto.getBirthday(), requestDto.getProfileContent(), requestDto.getAccount(), requestDto.getAccountName());
    }
}
