package com.habday.server.classes;

import com.habday.server.constants.CmnConst;
import com.habday.server.domain.fundingItem.FundingItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class Calculation {
    //주어진 날짜를 유닉스로 변경
    public Long getUnixTimeStamp(int year, int month, int date){
        log.info("getUnixTimeStamp: "+ year + " " + month + " " + date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        log.info("getUnixTimeStamp: "+  calendar.getTimeInMillis() / 1000);
        return calendar.getTimeInMillis() / 1000;
    }

    //현재 유닉스 시간 구하기
    public Long getCurrentUnixTime(){
        return System.currentTimeMillis() / 1000;
    }

    //펀딩 참여 후 아이템의 누적 금액 구하기
    public BigDecimal calTotalPrice(BigDecimal amount, BigDecimal totalPrice){
        if (totalPrice == null) {
            log.info("fundingService: totalPrice null임" + totalPrice);
            totalPrice = BigDecimal.ZERO;
        }
        return amount.add(totalPrice);
    }

    //펀딩 참여 후 아이템의 진행 퍼센트 구하기
    public int calFundingPercentage(BigDecimal totalPrice, BigDecimal goalPrice){
        return totalPrice.divide(goalPrice, 2, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(100)).intValue();
    }

    //예약결제 시간 계산
    public Date calPayDate(LocalDate localDate){
        Date toDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate);
        calendar.add(Calendar.DATE, CmnConst.paymentDelayDate);//펀딩 마감일이 생일 전날 -> 결제일은 생일날
        calendar.add(Calendar.MINUTE, CmnConst.paymentDelayMin);//펀딩 종료 다음날 30분 후에 결제
        return new Date(calendar.getTimeInMillis());
    }

    public Boolean isAfterTwoWeek(FundingItem item){
        LocalDate finishedDate = item.getFinishDate();//14일(생일 15일)
        LocalDate payDate = finishedDate.plusDays(CmnConst.paymentDelayDate);//15일
        LocalDate afterTwoWeek = payDate.plusDays(CmnConst.confirmLimitDate);//29일
        //15 16 17 18 19 20 21 22 23 24 25 26 27 28
        if (afterTwoWeek.compareTo(LocalDate.now()) <= 0){//29 <= 오늘
            log.info("isAfterTwoWeek(): 펀딩 인증 2주 지남" + finishedDate.compareTo(afterTwoWeek) + " " + afterTwoWeek + "," + finishedDate);
            return true;
        }else {
            log.info("isAfterTwoWeek(): 펀딩 인증 2주 이내" + finishedDate.compareTo(afterTwoWeek) + " " + afterTwoWeek  + "," + finishedDate);
            return false;
        }
    }

//    public Date addDate(Date baseDate, int addedUnit, int addedTime){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(baseDate);
//        calendar.add(addedUnit, addedTime);
//        return new Date(calendar.getTimeInMillis());
//    }
}
