package com.habday.server.domain.payment;

import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto.PaymentList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<PaymentList> findByMemberId(Long memberId);

    Payment findByCardNumberEnd(String cardNumberEnd);

    Long countByMemberId(Long memberId);
}
