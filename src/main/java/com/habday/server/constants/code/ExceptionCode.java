package com.habday.server.constants.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

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

    PARTICIPATE_FUNDING_SAVE_FAIL(BAD_REQUEST, "펀딩 참여에 실패했습니다"),
    PAY_SCHEDULING_FAIL(BAD_REQUEST, "결제 스케쥴 등록에 실패했습니다. 요청값을 확인바랍니다."),
    NO_FUNDING_IMG(BAD_REQUEST, "펀딩을 참여하기 위해서 이미지는 필수입니다."),
    NO_PAYMENT_EXIST(BAD_REQUEST, "선택한 결제수단에 해당하는 데이터가 없습니다. 결제수단 번호를 다시 확인해주세요."),
    PAY_SCHEDULING_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "결제 스케쥴 등록 중 결제 모듈 에러가 발생했습니다."),
    PAY_UNSCHEDULING_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "결제 스케쥴 등록 중 결제 모듈 에러가 발생했습니다."),
    SHOW_SCHEDULE_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "스케쥴 가져오기에 실패했습니다."),
    NO_FUNDING_MEMBER_ID(BAD_REQUEST, "존재하지 않는 펀딩 참여 내역 입니다."),
    ALREADY_CANCELED(BAD_REQUEST, "이미 취소된 펀딩 결제 내역 입니다."),
    WEBHOOK_FAIL(BAD_REQUEST, "예약 결제 실패"),
    NO_MEMBER_ID_SAVED(INTERNAL_SERVER_ERROR, "펀딩 생성 시 멤버id가 저장되지 않았습니다."),
    NO_FUNDING_STATE_EXISTS(BAD_REQUEST, "잘못된 펀딩 상태 입니다."),
    UNAUTHORIZED_IP(UNAUTHORIZED, "허가되지 않은 ip 입니다."),
    GET_PAY_INFO_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "일치하는 결제 정보가 없습니다."),
    NO_CORRESPONDING_AMOUNT(INTERNAL_SERVER_ERROR,"저장된 금액과 결제된 금액이 일치하지 않습니다."),
    DELETING_BILLING_KEY_FAIL_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "빌링키 삭제 중 결제 모듈 에러가 발생했습니다."),
//    DELETING_BILLING_KEY_FAIL();

    // 펀딩 종료
    NOT_FINISH_FUNDING(BAD_REQUEST, "펀딩이 아직 종료되지 않았습니다."),
    ALREADY_FINISHED_FUNDING(BAD_REQUEST, "이미 완료된 펀딩입니다."),
    FAIL_FINISH_FUNDING(BAD_REQUEST, "펀딩 목표 퍼센트에 달성하지 못했습니다."),

    FAIL_WHILE_UNSCHEDULING(INTERNAL_SERVER_ERROR, "펀딩 실패로 인한 예약 결제 취소 중 알 수 없는 에러가 발생했습니다."),

    FAIL_SENDING_MAIL(INTERNAL_SERVER_ERROR, "메일 전송에 실패했습니다."),
    FAIL_UPLOADING_IMG(INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    FUNDING_CONFIRM_EXCEEDED(INTERNAL_SERVER_ERROR, "펀딩 인증 기간이 지났습니다."),
    FUNDING_CONFIRM_NOT_NEEDED(INTERNAL_SERVER_ERROR, "실패한 펀딩이거나 펀딩 실패한 경우 펀딩 인증이 필요하지 않습니다."),
    FUNDING_CONFIRM_NOT_YET(INTERNAL_SERVER_ERROR, "완료되지 않은 펀딩은 인증을 진행할 수 없습니다.");


    private final HttpStatus status;
    private final String msg;
}

