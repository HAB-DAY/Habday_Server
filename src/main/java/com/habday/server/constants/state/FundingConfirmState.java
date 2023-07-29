package com.habday.server.constants.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FundingConfirmState {
    FALSE("아직 선물 인증 안함"),
    TRUE("선물 인증 끝남"),
    DONE("state 체크 끝");
    private final String msg;
}
