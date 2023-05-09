package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.constants.ExceptionCode;
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
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final VerifyIamportService verifyIamportService;
    private final PaymentRepository paymentRepository;
    @Transactional//예외 발생 시 롤백해줌
    public ParticipateFundingResponseDto participateFunding(ParticipateFundingRequest fundingRequestDto, Long memberId){
        Payment selectedPayment = paymentRepository.findById(fundingRequestDto.getPaymentId()).
                orElseThrow(() -> new CustomException(NO_PAYMENT_EXIST));
        /*FundingItem selectedFunding = fundingItemRepository.findById(fundingRequestDto.getFundingItemId())
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID)); //todo 스케쥴 시간을 프론트에서 받아올지는 고민임.
        Date schedule_at = java.sql.Date.valueOf(selectedFunding.getFinishDate());
        log.debug("participateFunding date: " + schedule_at);*/

        IamportResponse<List<Schedule>> scheduleResult =  verifyIamportService.noneAuthPaySchedule(
                NoneAuthPayScheduleRequestDto.builder()
                        .customer_uid(selectedPayment.getBillingKey())
                        .merchant_uid(verifyIamportService.createMerchantUid(fundingRequestDto.getFundingItemId(), memberId))
                        .schedule_at(fundingRequestDto.getFundingDate())
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

        //todo 저장할 때 NoneAuthPayScheduleRequestDto와 ParticipateFundingRequestDto 합쳐야 하는데..
        /*Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        fundingMemberRepository.save(FundingMember.builder()
                    .name(fundingRequestDto.getName())
                    .amount(fundingRequestDto.getAmount())
                    .message(fundingRequestDto.getMessage())
                    .fundingDate(fundingRequestDto.getFundingDate())
                    .paymentId(fundingRequestDto.getPaymentId())
                    .fundingItem(selectedFunding)
                    .member(member)
                    .build()*/

        return ParticipateFundingResponseDto.of(scheduleResult.getCode(), scheduleResult.getMessage());
    }
}
