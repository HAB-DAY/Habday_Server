package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduledPayState {
    ready("ready", "가상계좌 발급 후 결제 대기중"),
    paid("paid", "결제 완료"),
    fail("fail", "결제 실패");
    private final String msg;
    private final String descibe;
}
