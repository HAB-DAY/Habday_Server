package com.habday.server.service;

import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FundingService {
    private final FundingMemberRepository fundingMemberRepository;
    private final VerifyIamportService verifyIamportService;
    @Transactional//예외 발생 시 롤백해줌
    public void participateFunding(ParticipateFundingRequest fundingRequestDto, Long memberId){
        //fundingMemberRepository.save
        //verifyIamportService.noneAuthPaySchedule();
    }
}
