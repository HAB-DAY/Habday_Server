package com.habday.server.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    NO_MEMBER_ID(BAD_REQUEST, "존재하지 않는 사용자 정보입니다."),
    //빌링키 발급
    BILLING_KEY_SAVE_FAIL(BAD_REQUEST, "빌링키 저장에 실패했습니다. 누락된 정보가 없는지 확인해주세요"),
    CARD_NUMBER_LENGTH_INCORRECT(BAD_REQUEST, "카드 번호는 16자리를 입력해주세요"),
    CARD_ALREADY_EXIST(BAD_REQUEST, "이미 등록된 카드입니다"),
    BILLING_KEY_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "빌링키 발급 중 내부 에러가 발생했습니다"),
    //펀딩참여
    NO_FUNDING_ITEM_ID(BAD_REQUEST, "존재하지 않는 펀딩 아이템 입니다."),
    PARTICIPATE_FUNDING_SAVE_FAIL(BAD_REQUEST, "펀딩 참여에 실패했습니다"),
    PAY_SCHEDULING_FAIL(BAD_REQUEST, "결제 스케쥴 등록에 실패했습니다. 요청값을 확인바랍니다."),
    NO_FUNDING_IMG(BAD_REQUEST, "펀딩을 참여하기 위해서 이미지는 필수입니다.");

    private final HttpStatus status;
    private final String msg;
}

