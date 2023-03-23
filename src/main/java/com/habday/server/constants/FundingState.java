package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FundingState {
    PROGRESS("진행중인 펀딩"),
    SUCCESS("성공한 펀딩"),
    FAIL("실패한 펀딩");

    private final String msg;
}
