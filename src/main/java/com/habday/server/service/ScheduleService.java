package com.habday.server.service;

import com.habday.server.classes.Calculation;
import com.habday.server.classes.Common;
import com.habday.server.constants.state.FundingConfirmState;
import com.habday.server.constants.state.FundingState;
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
        List<FundingItem> fundingItems = fundingItemRepository.findByIsConfirmAndStatus(FundingConfirmState.FALSE, FundingState.SUCCESS);

        fundingItems.forEach((item -> {
            if (calculation.isAfterTwoWeek(item)){//afterTwoWeek >= LocalDate.now()이면 pass
                Member member = item.getMember();
                member.updateStatusSuspended();
                log.info("checkMemberState(): memberId: " + member.getId() + " itemId: " + item.getId() + " 기간 만료");
            }
        }));
    }
}
