package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    VERIFY_MEMBER_PROFILE_SUCCESS(OK, "사용자 프로필 수정을 성공했습니다"),
    CREATE_FUNDING_ITEM_SUCCESS(OK, "펀딩 생성에 성공했습니다."),
    CREATE_BILLING_KEY_SUCCESS(OK, "빌링키 발급 성공"),
    GET_PAYMENT_LISTS_SUCCESS(OK, "결제 수단 가져오기 성공");

    private final HttpStatus status;
    private final String msg;
}