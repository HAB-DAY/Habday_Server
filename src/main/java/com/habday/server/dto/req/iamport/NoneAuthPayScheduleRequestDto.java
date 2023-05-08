package com.habday.server.dto.req.iamport;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
public class NoneAuthPayScheduleRequestDto {//todo customer_uid와 merchant_uid는 서버에서 생성 예정 + schedule_at도 funding_item에서 가져오기
    private String customer_uid; //빌링키
    private String merchant_uid; //주문번호
    private Date schedule_at; //스케쥴 정보
    private BigDecimal amount; //상품 가격
    private String name; //상품 이름(펀딩 이름) 이것도 db에서 가져옴
    private String buyer_name;//구매자 정보(펀딩 참여자 정보)
    private String buyer_tel;
    private String buyer_email;

    private NoneAuthPayScheduleRequestDto(String customer_uid, String merchant_uid, Date schedule_at,
                    BigDecimal amount, String name, String buyer_name, String buyer_tel, String buyer_email){
        this.customer_uid = customer_uid;
        this.merchant_uid = merchant_uid;
        this.schedule_at = schedule_at;
        this.amount = amount;
        this.name = name;
        this.buyer_name = buyer_name;
        this.buyer_tel = buyer_tel;
        this.buyer_email = buyer_email;
    }

    public static NoneAuthPayScheduleRequestDto of(String customer_uid, String merchant_uid, Date schedule_at,
                     BigDecimal amount, String name, String buyer_name, String buyer_tel, String buyer_email){
        return new NoneAuthPayScheduleRequestDto(customer_uid, merchant_uid, schedule_at, amount, name, buyer_name, buyer_tel, buyer_email);
    }

    public String printRequest(){
        return /*"customer_uid: " + customer_uid + " merchant_uid: " + merchant_uid +
                " schedule_at: " + schedule_at +  " amount: " + amount + */" name: " + name +
                " buyer_name: " + buyer_name + " buyer_tel: " + buyer_tel + " buyer_email" + buyer_email;
    }
}
