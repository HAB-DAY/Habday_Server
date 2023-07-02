package com.habday.server.classes.implemented;

import com.habday.server.classes.Common;
import com.habday.server.constants.FundingState;
import com.habday.server.domain.member.Member;
import com.habday.server.interfaces.ListInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
public class HostedList extends Common implements ListInterface {

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
        return pointId == null?
                fundingItemRepository.findByStatusAndMemberOrderByIdDesc(FundingState.PROGRESS, member, page):
                fundingItemRepository.findByIdLessThanAndStatusAndMemberOrderByIdDesc(pointId, FundingState.PROGRESS, member, page);
    }

    @Override
    public List<HostedListDto> getFinishedList(Member member, Long pointId, Pageable page) {
        return pointId == null?
                fundingItemRepository.findByStatusNotAndMemberOrderByIdDesc(FundingState.PROGRESS, member, page):
                fundingItemRepository.findByIdLessThanAndStatusNotAndMemberOrderByIdDesc(pointId, FundingState.PROGRESS, member, page);
    }
}
