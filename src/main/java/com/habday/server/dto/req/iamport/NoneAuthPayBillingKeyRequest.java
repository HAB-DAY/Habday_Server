package com.habday.server.dto.req.iamport;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class NoneAuthPayBillingKeyRequest {
    private String payment_name;

    @NotNull(message = "카드번호를 입력해주세요.")
    @Size(min = 19, message = "카드번호는 - 를 포함한 16자리 입니다.")
    private String card_number;

    @NotNull(message = "유효기간을 입력해주세요.")
    private String expiry;

    @NotNull(message = "생년월일을 입력해주세요.")
    private String birth;

    @NotNull(message = "비밀번호 앞자리를 입력해주세요.")
    private String pwd_2digit;

    /*public String getPaymentName(){
        return payment_name;
    }*/
}
