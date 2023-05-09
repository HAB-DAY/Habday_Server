package com.habday.server.domain.fundingItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingItemRepository extends JpaRepository<FundingItem, Long> {
    Long countByIdAndMemberId(Long id, Long memberId);
}