package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    CREATE_BILLING_KEY_SUCCESS(OK, "빌링키 발급 성공");

    private final HttpStatus status;
    private final String msg;
}
