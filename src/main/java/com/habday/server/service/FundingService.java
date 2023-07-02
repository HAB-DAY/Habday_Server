package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Calculation;
import com.habday.server.classes.Common;
import com.habday.server.classes.UIDCreation;
import com.habday.server.classes.implemented.HostedList;
import com.habday.server.classes.implemented.HostedList.HostedListDto;
import com.habday.server.constants.FundingState;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.Payment;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.habday.server.dto.res.fund.GetHostingListResponseDto;
import com.habday.server.dto.res.fund.GetHostingListResponseDto.HostingList;
import com.habday.server.dto.res.fund.GetParticipatedListResponseDto;
import com.habday.server.dto.res.fund.GetParticipatedListResponseDto.ParticipatedListInterface;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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

        IamportResponse<List<Schedule>> scheduleResult =  iamportService.noneAuthPaySchedule(
                NoneAuthPayScheduleRequestDto.of(fundingRequestDto, selectedPayment.getBillingKey(), merchantUid, scheduleDate));
        log.debug("FundingService.participateFunding(): " + new Gson().toJson(scheduleResult));
        if (scheduleResult.getCode() !=0 ) {
            throw new CustomExceptionWithMessage(PAY_SCHEDULING_FAIL, scheduleResult.getMessage());
        }


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

    public GetHostingListResponseDto getHostingList(Long memberId, String status, Long pointId){
        ListInterface hostedList = new HostedList();
        List<HostedListDto> hostingLists;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        if (status == "PROGRESS"){
            hostingLists = hostedList.getProgressList(member, pointId, PageRequest.of(0, 10));
        }else{
            hostingLists = hostedList.getFinishedList(member, pointId, PageRequest.of(0, 10));
        }

        Long lastIdOfList = hostingLists.isEmpty() ? null : hostingLists.get(hostingLists.size() -1).getId();
        return new GetHostingListResponseDto(hostingLists, hasNext(lastIdOfList));
    }

    public GetParticipatedListResponseDto getParticipatedList(Long memberId, String status, Long pointId){
        List<ParticipatedListInterface> participatedLists;

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        if(status == "PROGRESS"){
            participatedLists = getProgressList_P(member, pointId, PageRequest.of(0, 10));
        }else{
            participatedLists = getFinishedList_p(member, pointId, PageRequest.of(0, 10));
        }

        Long lastIdOfList = participatedLists.isEmpty() ? null : participatedLists.get(participatedLists.size() -1).getFundingMemberId();
        return new GetParticipatedListResponseDto(participatedLists, hasNext(lastIdOfList));
    }

    private List<ParticipatedListInterface> getProgressList_P(Member member, Long pointId, Pageable page) {
        return pointId == null?
                fundingMemberRepository.getPagingListFirst_Progress(member, FundingState.PROGRESS, page) :
                fundingMemberRepository.getPagingListAfter_Progress(pointId, member, FundingState.PROGRESS, page);
    }

    private List<ParticipatedListInterface> getFinishedList_p(Member member, Long pointId, Pageable page) {
        return pointId == null?
                fundingMemberRepository.getPagingListFirst_Finished(member, FundingState.PROGRESS, page) :
                fundingMemberRepository.getPagingListAfter_Finished(pointId, member, FundingState.PROGRESS, page);
    }

    private Boolean hasNext(Long id){
        if(id == null) return false;
        return fundingItemRepository.existsByIdLessThan(id);
    }
}
