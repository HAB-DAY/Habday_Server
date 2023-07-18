package com.habday.server.dto.req.fund;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ConfirmationRequest {
    @NotNull(message="제목을 입력해주세요")
    private String title;

    @NotNull(message="하고 싶은 말을 입력헤주세요. ")
    private String message;
}
