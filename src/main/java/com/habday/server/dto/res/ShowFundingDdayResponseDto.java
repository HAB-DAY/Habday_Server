package com.habday.server.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ShowFundingDdayResponseDto {
    private String name;
    private Long leftday;

    @Builder
    public ShowFundingDdayResponseDto(String name, Long leftday){
        this.name = name;
        this.leftday = leftday;
    }
}
