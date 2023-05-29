package com.habday.server.dto.req.fund;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Getter
public class ParticipateFundingRequest {
    @NotNull(message="펀딩 아이템 아이디는 필수입니다.")
    private Long fundingItemId;

    private String name;

    @NotNull(message="전하고 싶은 메시지를 입력해주세요.")
    private String message;

    @NotNull(message="결제 시간은 필수입니다.")
    private Date fundingDate;

    @NotNull(message="금액은 필수입니다.")
    @Min(value = 101, message="최소 금액은 101원 입니다.")
    private BigDecimal amount;

    @NotNull(message="결제 수단을 선택해주세요.")
    private Long paymentId;

    @NotNull(message="구매자 이름을 입력해주세요.")
    private String buyer_name;//구매자 정보(펀딩 참여자 정보)
    @NotNull(message="구매자 전화번호를 입력해주세요.")
    private String buyer_tel;
    @NotNull(message="구매자 이메일을 입력해주세요.")
    private String buyer_email;
}
