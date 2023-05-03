package com.habday.server.dto.res.fund;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipateFundingResponseDto {
    private int code;
    private String message;

    public ParticipateFundingResponseDto(int code, String message){
        this.code = code;
        this.message = message;
    }

    public static ParticipateFundingResponseDto of(int code, String message){
        return new ParticipateFundingResponseDto(code, message);
    }

}
