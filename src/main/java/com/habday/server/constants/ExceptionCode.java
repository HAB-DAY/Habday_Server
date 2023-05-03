package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    NO_MEMBER_ID(BAD_REQUEST, "존재하지 않는 사용자 정보입니다."),
    BILLING_KEY_SAVE_FAIL(BAD_REQUEST, "빌링키 저장 시 누락된 요청 정보가 있습니다."),
    NO_FUNDING_ITEM_ID(BAD_REQUEST, "존재하지 않는 펀딩 아이템 입니다."),
    PARTICIPATE_FUNDING_SAVE_FAIL(BAD_REQUEST, "펀딩 참여에 실패했습니다"),
    PAY_SCHEDULING_FAIL(BAD_REQUEST, "결제 스케쥴 등록에 실패했습니다. 요청값을 확인바랍니다.");

    private final HttpStatus status;
    private final String msg;
}
