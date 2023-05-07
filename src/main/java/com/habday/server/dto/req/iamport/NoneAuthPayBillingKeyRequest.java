package com.habday.server.dto.req.iamport;

import lombok.Getter;

@Getter
public class NoneAuthPayBillingKeyRequest {
    private String payment_name;
    private String card_number;
    private String expiry;
    private String birth;
    private String pwd_2digit;

    /*public String getPaymentName(){
        return payment_name;
    }*/
    public String getCardNumber(){
        return card_number == null? "" : card_number;
    }
}
