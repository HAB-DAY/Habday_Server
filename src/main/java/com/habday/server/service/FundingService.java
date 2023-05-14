package com.habday.server.service;

import com.google.gson.Gson;
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
import com.habday.server.dto.res.fund.ParticipateFundingResponseDto;
import com.habday.server.exception.CustomException;
import com.habday.server.exception.CustomExceptionWithMessage;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.habday.server.constants.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingService {
    private final FundingMemberRepository fundingMemberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final MemberRepository memberRepository;
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

        IamportResponse<List<Schedule>> scheduleResult =  payService.noneAuthPaySchedule(
                NoneAuthPayScheduleRequestDto.builder()
                        .customer_uid(selectedPayment.getBillingKey())
                        .merchant_uid(merchantUid)
                        .schedule_at(scheduleDate)
                        .amount(fundingRequestDto.getAmount())
                        .name(fundingRequestDto.getName())
                        .buyer_name(fundingRequestDto.getBuyer_name())
                        .buyer_tel(fundingRequestDto.getBuyer_tel())
                        .buyer_email(fundingRequestDto.getBuyer_email())
                        .build()
        );
        log.debug("FundingService.participateFunding(): " + new Gson().toJson(scheduleResult));
        if (scheduleResult.getCode() !=0 ) {
            throw new CustomExceptionWithMessage(PAY_SCHEDULING_FAIL, scheduleResult.getMessage());
        }


        fundingMemberRepository.save(FundingMember.builder()
                .name(fundingRequestDto.getName())
                .amount(fundingRequestDto.getAmount())
                .message(fundingRequestDto.getMessage())
                .fundingDate(LocalDate.ofInstant(fundingRequestDto.getFundingDate().toInstant(), ZoneId.systemDefault()))
                .paymentId(fundingRequestDto.getPaymentId())
                .payment_status(ScheduledPayState.ready)
                .merchant_id(merchantUid)
                .imp_uid(selectedPayment.getBillingKey())
                .fundingItem(fundingItem)
                .member(member)
                .build());

        BigDecimal totalPrice = calTotalPrice(fundingRequestDto.getAmount(), fundingItem.getTotalPrice());
        int percentage = calFundingPercentage(totalPrice, fundingItem.getGoalPrice());
        fundingItem.updatePricePercentage(totalPrice, percentage);

        return ParticipateFundingResponseDto.of(scheduleResult.getCode(), scheduleResult.getMessage());
    }
}
