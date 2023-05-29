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
    GET_BILLING_KEY_FAIL(BAD_REQUEST, "빌링키 발급 실패."),
    CARD_ALREADY_EXIST(BAD_REQUEST, "이미 등록된 카드입니다."),
    BILLING_KEY_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "빌링키 발급 중 결제 모듈 에러가 발생했습니다."),

    //펀딩참여
    NO_FUNDING_ITEM_ID(BAD_REQUEST, "존재하지 않는 펀딩 아이템 입니다."),
    PAY_SCHEDULING_FAIL(BAD_REQUEST, "결제 스케쥴 등록에 실패했습니다. 요청값을 확인바랍니다."),
    NO_PAYMENT_EXIST(BAD_REQUEST, "선택한 결제수단에 해당하는 데이터가 없습니다. 결제수단 번호를 다시 확인해주세요."),
    PAY_SCHEDULING_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "결제 스케쥴 등록 중 결제 모듈 에러가 발생했습니다."),
    PAY_UNSCHEDULING_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "결제 스케쥴 등록 중 결제 모듈 에러가 발생했습니다."),
    NO_FUNDING_MEMBER_ID(BAD_REQUEST, "존재하지 않는 펀딩 참여 내역 입니다."),
    ALREADY_CANCELED(BAD_REQUEST, "이미 취소된 펀딩 결제 내역 입니다."),
    WEBHOOK_FAIL(BAD_REQUEST, "예약 결제 실패"),
    NO_MEMBER_ID_SAVED(INTERNAL_SERVER_ERROR, "펀딩 생성 시 멤버id가 저장되지 않았습니다.");

    private final HttpStatus status;
    private final String msg;
}

