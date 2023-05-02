package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    VERIFY_MEMBER_PROFILE_SUCCESS(OK, "사용자 프로필 수정을 성공했습니다");

    private final HttpStatus status;
    private final String msg;
}
