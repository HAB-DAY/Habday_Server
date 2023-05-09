package com.habday.server.dto.req.iamport;

import lombok.Getter;

@Getter
public class NoneAuthPayUnscheduleRequestDto {
    private String customer_uid;
    private String merchant_uid;
}
