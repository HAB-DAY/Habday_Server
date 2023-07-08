package com.habday.server.classes;

import com.habday.server.constants.CmnConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class Calculation {
    // TODO 현재 시간을 unixTimeStamp로 바꿔서 customeruid/merchantuid에 쓰기
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
        return totalPrice.divide(goalPrice, 2, BigDecimal.ROUND_CEILING).multiply(BigDecimal.valueOf(100)).intValue();
    }

    public Date calPayDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, CmnConst.paymentDelayMin);//펀딩 종료 30분 후에 결제
        return new Date(calendar.getTimeInMillis());
    }
}
