package com.habday.server.dto.req.iamport;

import lombok.Getter;

@Getter
public class NoneAuthPayBillingKeyRequest {
    private String customer_uid;
    private String card_number;
    private String expiry;
    private String birth;
    private String pwd_2digit;
}
