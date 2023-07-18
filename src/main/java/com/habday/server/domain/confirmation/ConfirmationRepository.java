package com.habday.server.domain.confirmation;

import com.habday.server.domain.fundingItem.FundingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    public Confirmation findByFundingItem(FundingItem fundingItem);
}
