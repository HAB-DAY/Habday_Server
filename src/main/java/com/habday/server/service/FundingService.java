package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Calculation;
import com.habday.server.classes.Common;
import com.habday.server.classes.UIDCreation;
import com.habday.server.classes.implemented.ParticipatedList;
import com.habday.server.config.S3Uploader;
import com.habday.server.constants.state.ScheduledPayState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.payment.Payment;
import com.habday.server.dto.req.fund.ConfirmationRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;

import static com.habday.server.constants.code.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingService extends Common {
    private final UIDCreation uidCreation;
    private final Calculation calculation;
    private final IamportService iamportService;
    private final S3Uploader s3Uploader;



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
        log.info("schedule date: " + scheduleDate);
        //아이앰포트에 스케쥴 걸기
        IamportResponse<List<Schedule>> scheduleResult =  iamportService.noneAuthPaySchedule(
                NoneAuthPayScheduleRequestDto.of(fundingRequestDto, selectedPayment.getBillingKey(), merchantUid, scheduleDate));
        log.info("FundingService.participateFunding(): " + new Gson().toJson(scheduleResult));
        if (scheduleResult.getCode() !=0 ) {
            throw new CustomExceptionWithMessage(PAY_SCHEDULING_FAIL, scheduleResult.getMessage());
        }

        //펀딩 참여 정보 저장
        fundingMemberRepository.save(FundingMember.of(fundingRequestDto, ScheduledPayState.ready, merchantUid, selectedPayment.getBillingKey()
                ,fundingItem, member));

        BigDecimal totalPrice = calculation.calTotalPrice(fundingRequestDto.getAmount(), fundingItem.getTotalPrice());
        int percentage = calculation.calFundingPercentage(totalPrice, fundingItem.getGoalPrice());

        //펀딩 아이템 누적 금액 update
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

    public GetListResponseDto getParticipateList(Long memberId, Long pointId){
        List <ParticipatedList.ParticipatedListInterface> participatedList;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        if(pointId == null)
            participatedList = fundingMemberRepository.getPagingListFirst(member, PageRequest.of(0, 10));
        else
            participatedList = fundingMemberRepository.getPagingListAfter(pointId, member, PageRequest.of(0, 10));

        Long lastIdOfList = participatedList.isEmpty() ? null : participatedList.get(participatedList.size() -1).getFundingMemberId();
        return new GetListResponseDto(participatedList, hasNext(lastIdOfList));
    }

    private Boolean hasNext(Long id){
        if(id == null) return false;
        return fundingItemRepository.existsByIdLessThan(id);
    }

    public void confirm(MultipartFile img, ConfirmationRequest request, Long memberId){

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        log.info("request: 2"+  request.getMessage());
        try {
            String fundingItemImgUrl = s3Uploader.upload(img, "images");
        } catch (IOException e) {
            throw new CustomException(FAIL_UPLOADING_IMG);
        }
    }
}
