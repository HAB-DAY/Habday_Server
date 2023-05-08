package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.constants.ExceptionCode;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.res.fund.ParticipateFundingResponseDto;
import com.habday.server.exception.CustomException;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    @Transactional//예외 발생 시 롤백해줌
    public ParticipateFundingResponseDto participateFunding(ParticipateFundingRequest fundingRequestDto, Long memberId) throws IamportResponseException, IOException {

        //todo save 예외처리 자세하게(없는 연관정보로 요청했다거나, 필요한 데이터 누락됐다거나 상황별 exception 자세하게
        //todo paymentId가 없으면 데이터 저장되면 안되는데....?


        FundingItem fundingItem = fundingItemRepository.findById(fundingRequestDto.getFundingItemId())
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        //todo int에는 null 값이 들어갈 수 없다! 위의 로직에서 오류 남.

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        //todo 같은 아이템에 중복 펀딩 가능하게!!!(jpa가 자동으로 save를 update 처리 함)
        try{
            fundingMemberRepository.save(FundingMember.builder()
                    .name(fundingRequestDto.getName())
                    .amount(fundingRequestDto.getAmount())
                    .message(fundingRequestDto.getMessage())
                    .fundingDate(fundingRequestDto.getFundingDate())
                    .paymentId(fundingRequestDto.getPaymentId())
                    .fundingItem(fundingItem)
                    .member(member)
                    .build()
            );
        }catch (Exception e){
            log.debug("FundingService save error: " + e);
            throw new CustomException(PARTICIPATE_FUNDING_SAVE_FAIL);
        }
        /*IamportResponse<List<Schedule>> scheduleResult =  verifyIamportService.noneAuthPaySchedule(fundingRequestDto.getScheduleData());
        log.debug("FundingService.participateFunding(): " + new Gson().toJson(scheduleResult));
        if (scheduleResult.getCode() == 1) {
            log.debug("FundingService.participateFunding 코드 1");
            throw new CustomException(PAY_SCHEDULING_FAIL);
        }//todo 에러용 BaseResponse 따로 만들기
        //todo iamport의 code와 messsage 담기(메시지 안나옴)

        return ParticipateFundingResponseDto.of(scheduleResult.getCode(), scheduleResult.getMessage());*/
        return null;//임시
    }
}
