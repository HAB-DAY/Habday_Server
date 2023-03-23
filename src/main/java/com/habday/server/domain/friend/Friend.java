package com.habday.server.domain.friend;

import com.habday.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "FRIEND")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendId")
    private Long id;

    @Column(nullable = false)
    private Long friendName;//친구의 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")//친구 신청 요청자
    private Member member;
}
