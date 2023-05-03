package com.habday.server.dto.req.fund;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
public class ParticipateFundingRequest {
    Long fundingItemId;
    String name;
    String message;
    Date fundingDate;
    BigDecimal amount;
    Long paymentId;

    public void of(){

    }
}
