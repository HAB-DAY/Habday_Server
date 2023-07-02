package com.habday.server.interfaces;

import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//HostedList/ParticipatedList
public interface ListInterface<T> {
    public List<T> getProgressList(FundingItemRepository repository, Member member, Long pointId, Pageable page);

    public List<T> getFinishedList(FundingItemRepository repository, Member member, Long pointId,  Pageable page);

    public Long getId();
}
