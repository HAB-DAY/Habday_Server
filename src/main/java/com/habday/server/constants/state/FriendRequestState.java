package com.habday.server.constants.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendRequestState {
    ACCEPT("친구 신청 수락"),
    DENIED("친구 신청 거절"),
    WAITING("친구 신청 대기중");

    private final String msg;
}
