package com.habday.server.service;

import com.google.gson.Gson;
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
import com.habday.server.exception.CustomException;
import com.habday.server.exception.CustomExceptionWithMessage;
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
public class FundingService {
    private final FundingMemberRepository fundingMemberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final MemberRepository memberRepository;
    private final IamportService iamportService;
    private final PayService payService;
    private final PaymentRepository paymentRepository;

    private BigDecimal calTotalPrice(BigDecimal amount, BigDecimal totalPrice){
        if (totalPrice == null) {
            log.debug("fundingService: totalPrice null임" + totalPrice);
            totalPrice = BigDecimal.ZERO;
        }
        return amount.add(totalPrice);
    }

    private int calFundingPercentage(BigDecimal totalPrice, BigDecimal goalPrice){
        return totalPrice.divide(goalPrice).multiply(BigDecimal.valueOf(100)).intValue();
    }

    private Date calPayDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 30);//펀딩 종료 30분 후에 결제
        return new Date(calendar.getTimeInMillis());
    }

    @Transactional//예외 발생 시 롤백해줌
    public ParticipateFundingResponseDto participateFunding(ParticipateFundingRequest fundingRequestDto, Long memberId) {
        String merchantUid = payService.createMerchantUid(fundingRequestDto.getFundingItemId(), memberId);

        Payment selectedPayment = paymentRepository.findById(fundingRequestDto.getPaymentId()).
                orElseThrow(() -> new CustomException(NO_PAYMENT_EXIST));
        FundingItem fundingItem = fundingItemRepository.findById(fundingRequestDto.getFundingItemId())
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        Date finishDateToDate = Date.from(fundingItem.getFinishDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date scheduleDate = calPayDate(finishDateToDate);//30분 더하기
        log.debug("schedule date: " + scheduleDate);

        IamportResponse<List<Schedule>> scheduleResult =  iamportService.noneAuthPaySchedule(
                NoneAuthPayScheduleRequestDto.of(fundingRequestDto, selectedPayment.getBillingKey(), merchantUid, scheduleDate));
        log.debug("FundingService.participateFunding(): " + new Gson().toJson(scheduleResult));
        if (scheduleResult.getCode() !=0 ) {
            throw new CustomExceptionWithMessage(PAY_SCHEDULING_FAIL, scheduleResult.getMessage());
        }


        fundingMemberRepository.save(FundingMember.of(fundingRequestDto, ScheduledPayState.ready, merchantUid, selectedPayment.getBillingKey()
                ,fundingItem, member));

        BigDecimal totalPrice = calTotalPrice(fundingRequestDto.getAmount(), fundingItem.getTotalPrice());
        int percentage = calFundingPercentage(totalPrice, fundingItem.getGoalPrice());
        fundingItem.updatePricePercentage(totalPrice, percentage);

        return ParticipateFundingResponseDto.of(scheduleResult.getCode(), scheduleResult.getMessage());
    }

    public ShowFundingContentResponseDto showFundingContent(Long fundingItemId){
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        Member member = fundingItem.getMember();
        if (member == null)
            throw new CustomException(NO_MEMBER_ID_SAVED);

        return ShowFundingContentResponseDto.of(fundingItem, member);
    }

    public GetHostingListResponseDto getHostItemList(Long memberId, String status, Long pointId){
        List<HostingList> hostingLists = getPagingList_H(pointId, memberId, PageRequest.of(0, 10), status);
        Long lastIdOfList = hostingLists.isEmpty() ? null : hostingLists.get(hostingLists.size() -1).getId();
        return new GetHostingListResponseDto(hostingLists, hasNext(lastIdOfList));
    }

    private List<HostingList> getPagingList_H(Long pointId, Long memberId, Pageable page, String status){
        /*
        * List용 status 2가지{
        *   완료(FINISHED): SUCCESS, FAIL
        *   진행중(PROGRESS): PROGRESS
        * }
        * */
        log.debug("펀딩 상태 체크: " + status);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        if (status.equals("PROGRESS")){
            return pointId == null?
                fundingItemRepository.findByStatusAndMemberOrderByIdDesc(FundingState.PROGRESS, member, page):
                fundingItemRepository.findByIdLessThanAndStatusAndMemberOrderByIdDesc(pointId, FundingState.PROGRESS, member, page);
        }
        else if(status.equals("FINISHED")){
            return pointId == null?
                fundingItemRepository.findByStatusNotAndMemberOrderByIdDesc(FundingState.PROGRESS, member, page):
                fundingItemRepository.findByIdLessThanAndStatusNotAndMemberOrderByIdDesc(pointId, FundingState.PROGRESS, member, page);
        }
        else throw new CustomException(NO_FUNDING_STATE_EXISTS);
    }

    private Boolean hasNext(Long id){
        if(id == null) return false;
        return fundingItemRepository.existsByIdLessThan(id);
    }

    private List<ParticipatedListInterface> getPagingList_P(Long pointId, Long memberId, Pageable page, String status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        if (status.equals("PROGRESS")){
            return pointId == null?
                    fundingMemberRepository.getPagingListFirst_Progress(member, FundingState.PROGRESS, page) :
                    fundingMemberRepository.getPagingListAfter_Progress(pointId, member, FundingState.PROGRESS, page);
        }
        else if(status.equals("FINISHED")){
            return pointId == null?
                    fundingMemberRepository.getPagingListFirst_Finished(member, FundingState.PROGRESS, page) :
                    fundingMemberRepository.getPagingListAfter_Finished(pointId, member, FundingState.PROGRESS, page);
        }
        else throw new CustomException(NO_FUNDING_STATE_EXISTS);
        //fundingMember & funding_item 테이블과 join 해서 fundingList에서 가져오는건 다 가져와야 함 +
        // funding_date, payment_status)
    }

    public GetParticipatedListResponseDto getParticipatedList(Long memberId, String status, Long pointId){
        List<ParticipatedListInterface> participatedLists = getPagingList_P(pointId, memberId, PageRequest.of(0, 10), status);
        Long lastIdOfList = participatedLists.isEmpty() ? null : participatedLists.get(participatedLists.size() -1).getFundingMemberId();
        return new GetParticipatedListResponseDto(participatedLists, hasNext(lastIdOfList));

    }
}
