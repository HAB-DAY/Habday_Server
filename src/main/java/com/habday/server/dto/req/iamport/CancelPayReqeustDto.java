package com.habday.server.dto.req.iamport;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CancelPayReqeustDto {
    private String merchant_uid;
    private String reason;
    private BigDecimal cancel_request_amount;
}
