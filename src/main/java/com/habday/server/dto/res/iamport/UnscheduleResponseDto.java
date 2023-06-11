package com.habday.server.dto.res.iamport;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UnscheduleResponseDto {
    private String merchant_uid;
    private String merchant_name;
    private BigDecimal amount;
    private LocalDate cancelDate;

    @Builder
    public UnscheduleResponseDto(String merchant_uid, String merchant_name, BigDecimal amount, LocalDate cancelDate){
        this.merchant_uid = merchant_uid;
        this.merchant_name = merchant_name;
        this.amount = amount;
        this.cancelDate = cancelDate;
    }
}
