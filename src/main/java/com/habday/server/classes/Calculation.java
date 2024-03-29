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

    public BigDecimal calCancelTotalPrice(BigDecimal amount, BigDecimal totalPrice){
        if (totalPrice == null) {
            log.info("fundingService: totalPrice null임" + totalPrice);
            totalPrice = BigDecimal.ZERO;
        }
        return totalPrice.subtract(amount);
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

    //펀딩 인증 & (memberState 변경: 이 함수 적용 안함)
    public Boolean isAfterTwoWeek(FundingItem item){
        LocalDate finishedDate = item.getFinishDate();//14일(생일 15일)
        //LocalDate payDate = finishedDate.plusDays(CmnConst.paymentDelayDate);//15일
        LocalDate afterTwoWeek = finishedDate.plusDays(CmnConst.confirmLimitDate);//28일
        //15 16 17 18 19 20 21 22 23 24 25 26 27 28
        if (afterTwoWeek.compareTo(LocalDate.now()) < 0){//28 < 29일(오늘)
            log.info("isAfterTwoWeek(): 펀딩 인증 2주 지남" + finishedDate.compareTo(afterTwoWeek) + " " + afterTwoWeek + "," + finishedDate);
            return true;
        }else {
            log.info("isAfterTwoWeek(): 펀딩 인증 2주 이내" + finishedDate.compareTo(afterTwoWeek) + " " + afterTwoWeek  + "," + finishedDate);
            return false;
        }
    }

    //수정/삭제(finishDate 당일부터 불가하게)
    public Boolean isOverFinishDate(LocalDate finishDate){
        if(LocalDate.now().compareTo(finishDate)>=0){//finishDate 포함
            log.info("isFinishDate: 오늘 >= 마감일 입니다.");
            return true;
        }else{
            log.info("isFinishDate: 마감일 전입니다.");
            return false;
        }
    }
    //인증시(finishDate이 지나야 인증)
    public Boolean isBeforeFinishDate(LocalDate finishDate){
        if (finishDate.compareTo(LocalDate.now()) >= 0){
            log.info("isBeforeFinishDate: 마감일 당일 혹은 마감일 전");
            return true;
        }
        else {
            log.info("isBeforeFinishDate: 마감일 이후");
            return false;
        }
    }

    public LocalDate calScheduleFinishDate(){
        return LocalDate.now().minusDays(CmnConst.paymentDelayDate);
    }

    public LocalDate calMemberStateFinishDate(){
        return LocalDate.now().minusDays(CmnConst.confirmLimitDate);
    }
}
