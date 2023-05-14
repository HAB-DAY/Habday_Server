package com.habday.server.domain.fundingMember;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingMemberRepository extends JpaRepository<FundingMember, Long> {
    Long countByFundingItemIdAndMemberId(Long id, Long memberId);
}
