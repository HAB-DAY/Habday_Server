package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Calculation;
import com.habday.server.classes.Common;
import com.habday.server.constants.CmnConst;
import com.habday.server.constants.CustomException;
import com.habday.server.constants.code.ExceptionCode;
import com.habday.server.constants.state.FundingConfirmState;
import com.habday.server.constants.state.FundingState;
import com.habday.server.constants.state.MemberState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.habday.server.constants.CmnConst.memberStateCron;
import static com.habday.server.constants.CmnConst.scheduleCron;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleService extends Common {
    private final MemberRepository memberRepository;
    private final Calculation calculation;
    private final FundingCloseService closeService;

    @Transactional
    @Scheduled(cron = scheduleCron) // "0 5 0 * * *" 매일 밤 0시 5분에 실행
    public void checkFundingState() {
        log.info("schedule 시작");
        List<FundingItem> overdatedFundings =  fundingItemRepository.findByStatusAndFinishDate(FundingState.PROGRESS, LocalDate.now());
        overdatedFundings.forEach(fundingItem -> {
            log.info("오늘 마감 fundingItem: " + fundingItem.getId());
            closeService.checkFundingSuccess(fundingItem);
        });
        log.info("schedule 끝");
    }

    /* 휴면 계정 확인
     * 1. 뭔가 배치를 돌면서 휴면 계정인지 체크
     * 2. 접속할 때 체크
     * */
    @Transactional
    @Scheduled(cron = memberStateCron)//매일 밤 12시
    public void checkMemberState(){
        List<FundingItem> fundingItems = //now > finishDate + 14 == now - 14 > finishDate
                fundingItemRepository.findByIsConfirmAndStatusAndFinishDateLessThan(FundingConfirmState.FALSE,
                        FundingState.SUCCESS, LocalDate.now().minusDays(CmnConst.confirmLimitDate));//데이터 많아지면 검색 범위를 지정해도 되지 않을까 너무 옛날꺼는 검색하지 않는다던지
        //성공한 펀딩 and 인증 기간 지남(14일 지남) and 펀딩 인증 false
        fundingItems.forEach((item -> {//for문의 범위를 줄여야 해!!
            log.info("item: " + item.getId() + " memberId: " + item.getMember().getId());
            Member member = memberRepository.findById(item.getMember().getId()).orElseGet(null);
            if(member != null && member.getStatus().equals(MemberState.AVAILABLE))
                member.updateStatusSuspended();
            item.updateIsConfirmDone();//false로 있으면 조건문에 걸릴 수 있으니
//            if (calculation.isAfterTwoWeek(item)){//afterTwoWeek >= LocalDate.now()이면 pass
//                Member member = item.getMember();
//                member.updateStatusSuspended();
//                log.info("checkMemberState(): memberId: " + member.getId() + " itemId: " + item.getId() + " 기간 만료");
//                item.updateIsConfirmDone();
//            }
        }));
    }
}
