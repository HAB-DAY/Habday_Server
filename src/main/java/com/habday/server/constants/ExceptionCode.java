package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    NO_MEMBER_ID(BAD_REQUEST, "존재하지 않는 사용자 정보입니다."),
    BILLING_KEY_SAVE_FAIL(BAD_REQUEST, "누락된 요청 정보가 있습니다.");

    private final HttpStatus status;
    private final String msg;
}
