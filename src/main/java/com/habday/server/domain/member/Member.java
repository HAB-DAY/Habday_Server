package com.habday.server.domain.member;

import com.habday.server.constants.FundingState;
import com.habday.server.constants.MemberState;
import com.habday.server.domain.payment.Payment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "MEMBER")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;

    @Column()
    private String name;

    @Column()
    private String nickName;

    @Column()
    private String birthday;

    @Column()
    private String profileImg;

    @Column()
    private String profileContent;

    @Column()
    private String email;

    @Column()
    private String refreshToken;

    @Column()
    private String account;

    @Column()
    private String accountName;

    @Column()
    @Enumerated(value = EnumType.STRING)
    private MemberState status;

    //빌더
    @Builder
    public Member(Long id, String name, String nickName, String birthday, String profileImg, String account, String accountName, String profileContent, String email) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.birthday = birthday;
        this.profileImg = profileImg;
        this.account = account;
        this.accountName = accountName;
        this.profileContent = profileContent;
        this.email = email;
    }

}
