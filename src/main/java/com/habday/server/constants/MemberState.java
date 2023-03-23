package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberState {
    AVAILABLE("사용 가능 계정"),
    SUSPENDED("사용 중지된 계정");

    private final String msg;
}
