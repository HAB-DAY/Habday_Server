package com.habday.server.dto.res.fund;

import com.habday.server.constants.FundingState;
import com.habday.server.constants.ScheduledPayState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetParticipatedListResponseDto {
    private List<ParticipatedListInterface> participatedLists;
    private Boolean hasNext;

    public GetParticipatedListResponseDto(List<ParticipatedListInterface> participatedLists, Boolean hasNext){
        this.participatedLists = participatedLists;
        this.hasNext = hasNext;
    }
    @Getter
    public static class ParticipatedList{
        private Long id; //FundingItem
        private String fundingItemImg; //FundingItem
        private String fundingName; //FundingItem
        private BigDecimal totalPrice; //FundingItem
        private LocalDate startDate; //FundingItem
        private LocalDate finishDate; //FundingItem
        private FundingState status; //FundingItem
        private LocalDate fundingDate; //FundingMember
        private ScheduledPayState payment_status; //FundingMember
        private String merchantId; //FundingMember

        public ParticipatedList(Long id, String fundingItemImg, String fundingName,
                           BigDecimal totalPrice, LocalDate startDate, LocalDate finishDate,
                           FundingState status, LocalDate fundingDate, ScheduledPayState payment_status){
            this.id = id;
            this.fundingItemImg = fundingItemImg;
            this.fundingName = fundingName;
            this.totalPrice = totalPrice;
            this.startDate = startDate;
            this.finishDate = finishDate;
            this.status = status;
            this.fundingDate = fundingDate;
            this.payment_status = payment_status;
        }
    }

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
}
