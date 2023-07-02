package com.habday.server.classes.implemented;

import com.habday.server.classes.Common;
import com.habday.server.constants.FundingState;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.member.Member;
import com.habday.server.interfaces.ListInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Component
@RequiredArgsConstructor
public class HostedList implements ListInterface {
    //HostedList 객체의 생성 시점과 빈주입을 한 fundingItemRepository의 생성 시점이 맞지 않아서 발생하는 문제
    public List<HostedListDto> lists;
    public final FundingItemRepository fundingItemRepository;
    @Getter
    public static class HostedListDto{
        private Long id; //FundingItem
        private String fundingItemImg; //FundingItem
        private String fundingName; //FundingItem
        private BigDecimal totalPrice; //FundingItem
        private LocalDate startDate; //FundingItem
        private LocalDate finishDate; //FundingItem
        private FundingState status; //FundingItem

        public HostedListDto(Long id, String fundingItemImg, String fundingName,
                           BigDecimal totalPrice, LocalDate startDate, LocalDate finishDate,
                           FundingState status){
            this.id = id;
            this.fundingItemImg = fundingItemImg;
            this.fundingName = fundingName;
            this.totalPrice = totalPrice;
            this.startDate = startDate;
            this.finishDate = finishDate;
            this.status = status;
        }
    }

    @Override
    public List<HostedListDto> getProgressList(Member member, Long pointId, Pageable page) {
        if(pointId == null)
            return lists = fundingItemRepository.findByStatusAndMemberOrderByIdDesc(FundingState.PROGRESS, member, page);
        else
            return lists =fundingItemRepository.findByIdLessThanAndStatusAndMemberOrderByIdDesc(pointId, FundingState.PROGRESS, member, page);
    }

    @Override
    public List<HostedListDto> getFinishedList( Member member, Long pointId, Pageable page) {
        if(pointId == null)
            return lists = fundingItemRepository.findByStatusNotAndMemberOrderByIdDesc(FundingState.PROGRESS, member, page);
        else
            return lists = fundingItemRepository.findByIdLessThanAndStatusNotAndMemberOrderByIdDesc(pointId, FundingState.PROGRESS, member, page);
    }

    @Override
    public Long getId(){
        return lists.get(lists.size() -1).getId();
    }
}
