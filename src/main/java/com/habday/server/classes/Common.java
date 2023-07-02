package com.habday.server.classes;

import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.service.IamportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class Common {
    @Autowired
    public FundingMemberRepository fundingMemberRepository;
    @Autowired
    public FundingItemRepository fundingItemRepository;
    @Autowired
    public MemberRepository memberRepository;
    @Autowired
    public PaymentRepository paymentRepository;
}
