package com.habday.server.constants.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduledPayState {
    ready("ready", "가상계좌 발급 후 결제 대기중"),
    paid("paid", "결제 완료"),
    fail("fail", "결제 실패"),
    cancel("cancel", "결제 취소");
    private final String msg;
    private final String descibe;
}
