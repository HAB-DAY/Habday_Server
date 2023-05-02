package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    MEMBER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NO_MEMBER_PROFILE(BAD_REQUEST, "사용자 정보 수정이 잘못되었습니다.");
    private final HttpStatus status;
    private final String msg;
}
