package com.habday.server.dto.req.iamport;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class NoneAuthPayUnscheduleRequestDto {
    /*@NotNull(message = "취소를 위해서는 결제 수단 id가 필요합니다.")
    private Long payment_id;*/

    @NotNull(message = "취소를 위해서는 결제 상품 id가 필요합니다.")
    private Long fundingMemberId;

    @NotNull(message="예약 결제 취소 이유를 입력해주세요.")
    private String reason;

    public NoneAuthPayUnscheduleRequestDto(Long fundingMemberId, String reason){
        this.fundingMemberId = fundingMemberId;
        this.reason = reason;
    }
}
