package com.habday.server.dto.res.fund;

import com.habday.server.constants.FundingState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetHostingListResponseDto {
    private List<HostingList> hostingLists;
    private Boolean hasNext;

    public GetHostingListResponseDto(List<HostingList> hostingLists, Boolean hasNext){
        this.hostingLists = hostingLists;
        this.hasNext = hasNext;
    }

    @Getter
    public static class HostingList{
        private Long id; //FundingItem
        private String fundingItemImg; //FundingItem
        private String fundingName; //FundingItem
        private BigDecimal totalPrice; //FundingItem
        private LocalDate startDate; //FundingItem
        private LocalDate finishDate; //FundingItem
        private FundingState status; //FundingItem

        public HostingList(Long id, String fundingItemImg, String fundingName,
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
}
