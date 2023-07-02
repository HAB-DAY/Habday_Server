package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Calculation;
import com.habday.server.classes.Common;
import com.habday.server.classes.UIDCreation;
import com.habday.server.classes.implemented.HostedList;
import com.habday.server.classes.implemented.ParticipatedList;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.payment.Payment;
import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.habday.server.dto.res.fund.GetListResponseDto;
import com.habday.server.dto.res.fund.ParticipateFundingResponseDto;
import com.habday.server.dto.res.fund.ShowFundingContentResponseDto;
import com.habday.server.dto.res.fund.ShowFundingContentResponseDto.FundingParticipantList;
import com.habday.server.exception.CustomException;
import com.habday.server.exception.CustomExceptionWithMessage;
import com.habday.server.interfaces.ListInterface;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.habday.server.constants.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingService extends Common {
    private final UIDCreation uidCreation;
    private final Calculation calculation;
    private final IamportService iamportService;


    @Transactional//예외 발생 시 롤백해줌
    public ParticipateFundingResponseDto participateFunding(ParticipateFundingRequest fundingRequestDto, Long memberId) {
        String merchantUid = uidCreation.createMerchantUid(fundingRequestDto.getFundingItemId(), memberId);
        Payment selectedPayment = paymentRepository.findById(fundingRequestDto.getPaymentId()).
                orElseThrow(() -> new CustomException(NO_PAYMENT_EXIST));
        FundingItem fundingItem = fundingItemRepository.findById(fundingRequestDto.getFundingItemId())
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        Date finishDateToDate = Date.from(fundingItem.getFinishDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date scheduleDate = calculation.calPayDate(finishDateToDate);//30분 더하기
        log.debug("schedule date: " + scheduleDate);
        //아이앰포트
        IamportResponse<List<Schedule>> scheduleResult =  iamportService.noneAuthPaySchedule(
                NoneAuthPayScheduleRequestDto.of(fundingRequestDto, selectedPayment.getBillingKey(), merchantUid, scheduleDate));
        log.debug("FundingService.participateFunding(): " + new Gson().toJson(scheduleResult));
        if (scheduleResult.getCode() !=0 ) {
            throw new CustomExceptionWithMessage(PAY_SCHEDULING_FAIL, scheduleResult.getMessage());
        }

        //저장
        fundingMemberRepository.save(FundingMember.of(fundingRequestDto, ScheduledPayState.ready, merchantUid, selectedPayment.getBillingKey()
                ,fundingItem, member));

        BigDecimal totalPrice = calculation.calTotalPrice(fundingRequestDto.getAmount(), fundingItem.getTotalPrice());
        int percentage = calculation.calFundingPercentage(totalPrice, fundingItem.getGoalPrice());
        fundingItem.updatePricePercentage(totalPrice, percentage);

        return ParticipateFundingResponseDto.of(scheduleResult.getCode(), scheduleResult.getMessage());
    }

    public ShowFundingContentResponseDto showFundingContent(Long fundingItemId){
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        Member member = fundingItem.getMember();
        if (member == null)
            throw new CustomException(NO_MEMBER_ID_SAVED);

        return ShowFundingContentResponseDto.of(fundingItem, member, getParticipantList(fundingItem));
    }

    public List<FundingParticipantList> getParticipantList(FundingItem fundingItem) {
        return fundingMemberRepository.findByFundingItem(fundingItem);
    }

    /*
     * 참여/호스팅 목록 조회 status 2가지{
     *   완료(FINISHED): SUCCESS, FAIL
     *   진행중(PROGRESS): PROGRESS
     * }
     * */

    public <T> GetListResponseDto getList(ListInterface listInterface, Long memberId, String status, Long pointId){
        List<T> hostingLists;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        if (status == "PROGRESS"){
            hostingLists = listInterface.getProgressList(member, pointId, PageRequest.of(0, 10));
        }else{
            hostingLists = listInterface.getFinishedList(member, pointId, PageRequest.of(0, 10));
        }

        Long lastIdOfList = hostingLists.isEmpty() ? null : listInterface.getId();
        return new GetListResponseDto(hostingLists, hasNext(lastIdOfList));
    }

    private Boolean hasNext(Long id){
        if(id == null) return false;
        return fundingItemRepository.existsByIdLessThan(id);
    }

    // 펀딩 기간 만료 후, 펀딩 목표 퍼센트 달성 했는지 여부 확인 로직
    public void checkFundingFinishDate(FundingItem fundingItem){
        LocalDate now = LocalDate.now(); //현재 날짜 구하기
        System.out.println("now^^ " + now.isEqual(fundingItem.getFinishDate()));

        if (now.isEqual(fundingItem.getFinishDate())) { // 현재날짜가 펀딩 종료 날짜일 경우
            checkFundingGoalPercent(fundingItem.getItemPrice(), fundingItem.getTotalPrice(), fundingItem.getGoalPrice());
            //status SUCCESS로 바꾸기
        } else {
            throw new CustomException(NOT_FINISH_FUNDING); // 펀딩이 아직 종료되지 않음
        }
    }

    public void checkFundingGoalPercent(BigDecimal itemPrice, BigDecimal totalPrice, BigDecimal goalPrice) {
        BigDecimal goalPercent = goalPrice.divide(itemPrice, BigDecimal.ROUND_DOWN); //최소목표퍼센트
        BigDecimal realPercent = totalPrice.divide(itemPrice, BigDecimal.ROUND_DOWN); // 실제달성퍼센트

        System.out.println("goalPercent^^ " + goalPercent);
        System.out.println("realPercent^^ " + realPercent);
        System.out.println("realPercent.compareTo(goalPercent)^^ : " + realPercent.compareTo(goalPercent));
        if (realPercent.compareTo(goalPercent) == 0 ||  realPercent.compareTo(goalPercent) == 1) { // 펀딩 최소 목표 퍼센트에 달성함
            System.out.println("최소 목표퍼센트 이상 달성함");
        } else { // 펀딩 최소 목표 퍼센트에 달성 못함
            throw new CustomException(FAIL_FINISH_FUNDING);
        }
    }
}
