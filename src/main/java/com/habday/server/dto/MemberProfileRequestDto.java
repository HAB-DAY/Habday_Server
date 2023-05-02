package com.habday.server.dto;

import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberProfileRequestDto {
    private Long id;
    private String nickName;
    private String birthday;
    private String profileContent;
    private String account;
    private String accountName;


    @Builder
    public MemberProfileRequestDto(Long id, String nickName, String birthday, String profileContent, String account, String accountName) {
        this.id = id;
        this.nickName = nickName;
        this.birthday = birthday;
        this.profileContent = profileContent;
        this.account = account;
        this.accountName = accountName;
    }

    public Member toVerifyMemberProfile(Long id, String nickName, String birthday, String profileContent, String account, String accountName) {
        return Member.builder()
                .id(id)
                .nickName(nickName)
                .birthday(birthday)
                .profileContent(profileContent)
                .account(account)
                .accountName(accountName)
                .build();
    }

    public static MemberProfileRequestDto of(Long id, String nickName, String birthday, String profileContent, String account, String accountName){
        return new MemberProfileRequestDto(id, nickName, birthday, profileContent, account, accountName);
    }
}
