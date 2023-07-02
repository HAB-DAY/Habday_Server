package com.habday.server.service;

import com.habday.server.classes.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduleService extends Common {
    @Scheduled(cron = "0 5 0 * * *") // 매일 밤 0시 5분에 실행
    public void checkFundingState() {
        /*
        * 1. 당일 밤 12시 반에 마감이 펀딩 확인
        * 2. 목표 퍼센트를 달성했는지 확인하기
        * 3. 퍼센트 달성 실패 시 예약 취소하기
        *   - fundingItem status fail로 업데이트
        *   - fundingMember status cancel로 업데이트
        *   - 펀딩 실패 메일 보내기
        * 4. 퍼센트 달성 성공 시 fundingItem status success로 업데이트
        *   - 펀딩 성공 메일 보내기
        * */
       log.info("Hello CoCo World!");
    }

    // naver jwt를 디코드 하면 그 안에 뭐가 들어있지?
    //스케줄 하나 더
    @Scheduled(cron = "0 40 0 * * *") // 매일 밤 0시 40분에 실행
    public void updatePaymentState() {
        /*
         * 당일 마감된 펀딩 중에 status가 success인 건에 한해
         * fundingMember의 state 확인하고 payState update 하기 -> 나중에 주문 테이블도 쪼개야 하는데
         * 실패 시 실패 메일(or 카톡) 보내고 수동으로 결제하게 하기(7일 안에 재결제 가능하게)
         * */
    }

}