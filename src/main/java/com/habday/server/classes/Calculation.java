package com.habday.server.classes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class Calculation {
    public Long getUnixTimeStamp(int year, int month, int date){
        log.debug("getUnixTimeStamp: "+ year + " " + month + " " + date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        log.debug("getUnixTimeStamp: "+  calendar.getTimeInMillis() / 1000);
        return calendar.getTimeInMillis() / 1000;
    }

    public BigDecimal calTotalPrice(BigDecimal amount, BigDecimal totalPrice){
        if (totalPrice == null) {
            log.debug("fundingService: totalPrice null임" + totalPrice);
            totalPrice = BigDecimal.ZERO;
        }
        return amount.add(totalPrice);
    }

    public int calFundingPercentage(BigDecimal totalPrice, BigDecimal goalPrice){
        return totalPrice.divide(goalPrice).multiply(BigDecimal.valueOf(100)).intValue();
    }

    public Date calPayDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 30);//펀딩 종료 30분 후에 결제
        return new Date(calendar.getTimeInMillis());
    }
}