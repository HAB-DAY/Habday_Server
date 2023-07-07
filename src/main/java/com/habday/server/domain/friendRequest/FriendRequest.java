package com.habday.server.domain.friendRequest;

import com.habday.server.constants.state.FriendRequestState;
import com.habday.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "FRIEND_REQUEST")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestId")
    private Long id;

    @Column(nullable = false)
    private LocalDate requestDate;

    @Column(nullable = false)
    private Long requestMember;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FriendRequestState status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;
}// todo 친구 요청 신청 목록, 요청 수신자의 목록
