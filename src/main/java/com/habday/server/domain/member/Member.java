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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column()
    private String profileImg;

    @Column()
    private String profileContent;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String refreshToken;

    @Column()
    private String account;

    @Column()
    private String accountName;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberState status;

    //빌더
    @Builder
    public Member(String name, String nickName, LocalDate birthday, String profileImg, String profileContent, String email) {
        this.name = name;
        this.nickName = nickName;
        this.birthday = birthday;
        this.profileImg = profileImg;
        this.profileContent = profileContent;
        this.email = email;
    }

}
