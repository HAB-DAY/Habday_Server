package com.habday.server.classes.implemented;

import com.habday.server.constants.FundingState;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.domain.fundingMember.FundingMemberRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.interfaces.ListInterface;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ParticipatedList implements ListInterface<ParticipatedList.ParticipatedListInterface, FundingMemberRepository> {
    List<ParticipatedListInterface> lists;

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
    public List<ParticipatedListInterface> getProgressList(FundingMemberRepository repository, Member member, Long pointId, Pageable page) {
        if(pointId == null)
            lists = repository.getPagingListFirst_Progress(member, FundingState.PROGRESS, page);
        else
            lists = repository.getPagingListAfter_Progress(pointId, member, FundingState.PROGRESS, page);
        return lists;
    }

    @Override
    public List<ParticipatedListInterface> getFinishedList(FundingMemberRepository repository, Member member, Long pointId, Pageable page) {
        if(pointId == null)
            lists = repository.getPagingListFirst_Finished(member, FundingState.PROGRESS, page);
        else
            lists = repository.getPagingListAfter_Finished(pointId, member, FundingState.PROGRESS, page);
        return lists;
    }

    @Override
    public Long getId() {
        return lists.get(lists.size() -1).getFundingMemberId();
    }
}
