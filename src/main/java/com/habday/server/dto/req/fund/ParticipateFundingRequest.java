package com.habday.server.dto.req.fund;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ParticipateFundingRequest {
    Long fundingItemId;
    String name;
    String message;
    LocalDate fundingDate;
    BigDecimal amount;
    Long paymentId;

    public void of(){

    }
}
