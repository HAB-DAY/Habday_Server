package com.habday.server.config.auth.dto;

import com.habday.server.domain.member.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionMember implements Serializable { //세션에 사용자 정보를 저장하기 위한 Dto 클래스

    private String name;
    private String email;
    private String profileImg;

    public SessionMember(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.profileImg = member.getProfileImg();
    }
}
