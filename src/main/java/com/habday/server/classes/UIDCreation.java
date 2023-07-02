package com.habday.server.classes;

import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UIDCreation extends Common{

    public String createCustomerUid(Long memberId){
        Long paymentNum = paymentRepository.countByMemberId(memberId)+1;
        return "cus" + memberId + "_p" + paymentNum;//ex) cus2_p2
    }

    public String createMerchantUid(Long fundingItemId, Long memberId){
        Long itemNum = fundingMemberRepository.countByFundingItemIdAndMemberId(fundingItemId, memberId) + 8;
        return "mer" + fundingItemId + "_m" + memberId + "_i" + itemNum;//특정 아이템에 멤버 참여 횟수 정하기 ex)mer1_m2_i2
    }
}
