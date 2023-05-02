package com.habday.server.service;

import com.habday.server.constants.CustomException;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.MemberProfileRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.habday.server.constants.ExceptionCode.MEMBER_NOT_FOUND;
import static com.habday.server.constants.SuccessCode.VERIFY_MEMBER_PROFILE_SUCCESS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // member 프로필 정보 DB에 저장

}
