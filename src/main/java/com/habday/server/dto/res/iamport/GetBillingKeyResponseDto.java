package com.habday.server.dto.res.iamport;

import com.siot.IamportRestClient.response.BillingCustomer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetBillingKeyResponseDto {
    private String payment_name;
    private String customer_uid;

    public GetBillingKeyResponseDto(String payment_name, String customer_uid){
        this.payment_name = payment_name;
        this.customer_uid = customer_uid;
    }

    public static GetBillingKeyResponseDto of(String payment_name, String customer_uid ){
        return new GetBillingKeyResponseDto(payment_name, customer_uid);
    }
}
