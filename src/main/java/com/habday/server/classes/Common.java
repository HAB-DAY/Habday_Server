package com.habday.server.classes;

import com.google.gson.Gson;
import com.habday.server.domain.confirmation.Confirmation;
import com.habday.server.domain.confirmation.ConfirmationRepository;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.service.IamportService;
import com.habday.server.web.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Component
public class Common {
    @Autowired
    public FundingMemberRepository fundingMemberRepository;

    @Autowired
    public PaymentRepository paymentRepository;

    @Autowired
    public ConfirmationRepository confirmationRepository;

    @Autowired
    public FundingItemRepository fundingItemRepository;

    @Autowired
    public JwtService jwtService;
}
