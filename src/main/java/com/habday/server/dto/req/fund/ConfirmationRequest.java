package com.habday.server.dto.req.fund;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ConfirmationRequest {
    @NotNull(message="사진을 넣어주세요")
    private String image;

    @NotNull(message="하고 싶은 말을 입력헤주세요. ")
    private String message;

    @NotNull(message="펀딩 아이템 번호는 필수입니다. ")
    private String fundingItemId;
}
