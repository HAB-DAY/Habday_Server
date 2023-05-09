package com.habday.server.dto.req.member;

import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class MemberProfileRequestDto {
    private String nickName;
    private LocalDate birthday;
    private String profileContent;
    private String account;
    private String accountName;

    /*
    public MemberProfileRequestDto(String nickName, String birthday, String profileContent, String account, String accountName) {
        // this.nickName = nickName;
    }

    public static MemberProfileRequestDto of(String nickName, String birthday, String profileContent, String account, String accountName){
        return new MemberProfileRequestDto(nickName, birthday, profileContent, account, accountName);
    }*/
}
