package com.habday.server.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberProfileRequestDto {
    private String birthday;
    private String accountName; // banckName
    private String account; //accountNumber

    /*
    public MemberProfileRequestDto(String nickName, String birthday, String profileContent, String account, String accountName) {
        // this.nickName = nickName;
    }

    public static MemberProfileRequestDto of(String nickName, String birthday, String profileContent, String account, String accountName){
        return new MemberProfileRequestDto(nickName, birthday, profileContent, account, accountName);
    }*/
}
