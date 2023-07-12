package com.habday.server.constants.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    CREATE_BILLING_KEY_SUCCESS(OK, "빌링키 발급 성공"),
    GET_PAYMENT_LISTS_SUCCESS(OK, "결제 수단 가져오기 성공"),
    PARTICIPATE_FUNDING_SUCCESS(OK,"펀딩 참여 완료"),
    VERIFY_MEMBER_PROFILE_SUCCESS(OK, "사용자 프로필 수정을 성공했습니다"),
    CREATE_FUNDING_ITEM_SUCCESS(OK, "펀딩 생성에 성공했습니다."),
    PAY_UNSCHEDULING_SUCCESS(OK, "펀딩 예약 결제 취소에 성공했습니다."),
    SHOW_FUNDING_CONTENT_SUCCESS(OK, "펀딩 아이템 정보 조회에 성공했습니다."),
    GET_FUNDING_LIST_SUCCESS(OK, "참여한 펀딩 정보 조회에 성공했습니다."),
    GET_HOSTING_LIST_SUCCESS(OK, "주최한 펀딩 정보 조회에 성공했습니다."),
    DELETING_BILLING_KEY_SUCCESS(OK, "빌링키 삭제에 성공했습니다.");

    private final HttpStatus status;
    private final String msg;
}