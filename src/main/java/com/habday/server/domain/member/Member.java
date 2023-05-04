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
import java.time.LocalDateTime;

@Getter //@Data
@NoArgsConstructor
@Entity
@Table(name = "MEMBER")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;

    //@Column(nullable = false)
    @Column()
    private String name;

    private String password;

    //@Column(nullable = false)
    @Column()
    private String nickName;

    //@Column(nullable = false)
    @Column()
    private String birthday;

    @Column()
    private String profileImg;

    @Column()
    private String profileContent;

    //@Column(nullable = false)
    @Column()
    private String email;

    @Column()
    private String account;

    @Column()
    private String accountName;

    private LocalDateTime createTime;

    //@Column(nullable = false)
    @Column()
    @Enumerated(value = EnumType.STRING)
    private MemberState status;

    /*@Column()
    @Enumerated(value = EnumType.STRING)
    //@Column(nullable = false)
    private Role role;*/

    private String roles;
    private String provider;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "refreshToken")
    private RefreshToken jwtRefreshToken;

    //빌더
    @Builder
    public Member(Long id, String name, String password, String nickName, String birthday, String profileImg, String profileContent, String email, String account, String accountName, LocalDateTime createTime, String roles, String provider) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.nickName = nickName;
        this.birthday = birthday;
        this.profileImg = profileImg;
        this.profileContent = profileContent;
        this.email = email;
        this.account = account;
        this.accountName = accountName;
        this.createTime = createTime;
        this.roles = roles;
        this.provider = provider;
    }

    public Member update(String name, String email, String profileImg) {
        this.name = name;
        this.email = email;
        this.profileImg = profileImg;

        return this;
    }

    /*public String getRoleKey() {
        return this.role.getKey();
    }*/

    /**
     *  refresh 생성자, setter
     */
    /*public void createRefreshToken(RefreshToken refreshToken) {
        this.jwtRefreshToken = refreshToken;
    }
    public void SetRefreshToken(String refreshToken) {
        this.jwtRefreshToken.setRefreshToken(refreshToken);
    }*/

    /**
     * 사용자가 다양한 권한을 가지고 있을수 있음
     */
    /*public List<String> getRoleList() {
        if (this.roles.length() > 0) {
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }*/

}
