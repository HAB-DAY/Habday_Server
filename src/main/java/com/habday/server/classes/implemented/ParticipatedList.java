package com.habday.server.classes.implemented;

import com.habday.server.classes.Common;
import com.habday.server.constants.FundingState;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.interfaces.ListInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ParticipatedList implements ListInterface {
    List<ParticipatedListInterface> lists;
    public final FundingMemberRepository fundingMemberRepository;

    public interface ParticipatedListInterface{
        Long getFundingMemberId(); //FundingMember
        String getFundingName(); //FundingItem
        String getCreatorName(); //Member
        BigDecimal getFundingAmount(); //FundingMember
        String getFundingItemImg();
        //LocalDate getStartDate(); //FundingItem
        //LocalDate getFinishDate(); //FundingItem
        FundingState getFundingStatus(); //FundingItem
        LocalDate getFundingDate(); //FundingMember
        ScheduledPayState getPayment_status(); //FundingMember
        String getMerchantId(); //FundingMember
    }

    @Override
    public List<ParticipatedListInterface> getProgressList(Member member, Long pointId, Pageable page) {
        if(pointId == null)
            return lists = fundingMemberRepository.getPagingListFirst_Progress(member, FundingState.PROGRESS, page);
        else
            return lists = fundingMemberRepository.getPagingListAfter_Progress(pointId, member, FundingState.PROGRESS, page);
    }

    @Override
    public List<ParticipatedListInterface> getFinishedList(Member member, Long pointId, Pageable page) {
        if(pointId == null)
            return lists = fundingMemberRepository.getPagingListFirst_Finished(member, FundingState.PROGRESS, page);
        else
            return lists = fundingMemberRepository.getPagingListAfter_Finished(pointId, member, FundingState.PROGRESS, page);
    }

    @Override
    public Long getId() {
        return lists.get(lists.size() -1).getFundingMemberId();
    }
}
