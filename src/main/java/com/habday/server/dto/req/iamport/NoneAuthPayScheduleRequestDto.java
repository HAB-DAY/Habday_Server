package com.habday.server.dto.req.iamport;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
public class NoneAuthPayScheduleRequestDto {
    private String customer_uid; //빌링키
    private String merchant_uid; //상품번호
    private Date schedule_at; //스케쥴 정보
    private BigDecimal amount; //상품 가격
    private String name; //상품 이름(펀딩 이름)
    private String buyer_name;//구매자 정보(펀딩 참여자 정보)
    private String buyer_tel;
    private String buyer_email;

    public String printRequest(){
        return "customer_uid: " + customer_uid + " merchant_uid: " + merchant_uid +
                " schedule_at: " + schedule_at +  " amount: " + amount + " name: " + name +
                " buyer_name: " + buyer_name + " buyer_tel: " + buyer_tel + " buyer_email" + buyer_email;
    }
}
