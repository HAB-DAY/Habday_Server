package com.habday.server.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.habday.server.constants.state.FundingState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.habday.server.constants.state.FundingState.PROGRESS;

@Getter
@NoArgsConstructor
public class CreateFundingItemRequestDto {

    private String fundingName;
    private String fundDetail;
    private BigDecimal itemPrice;
    private BigDecimal goalPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @Builder
    public CreateFundingItemRequestDto(String fundingName, String fundDetail, BigDecimal itemPrice, BigDecimal goalPrice, LocalDate startDate, LocalDate finishDate) {
        this.fundingName = fundingName;
        this.fundDetail = fundDetail;
        this.itemPrice = itemPrice;
        this.goalPrice = goalPrice;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }
    public FundingItem toCreateFundingItem(String fundingItemImg, String fundingName, String fundDetail, BigDecimal itemPrice, BigDecimal goalPrice, LocalDate startDate, LocalDate finishDate, Member member) {
        return FundingItem.builder()
                .fundingItemImg(fundingItemImg)
                .fundingName(fundingName)
                .fundDetail(fundDetail)
                .itemPrice(itemPrice)
                .goalPrice(goalPrice)
                .startDate(startDate)
                .finishDate(finishDate)
                .status(FundingState.PROGRESS)
                .member(member)
                .build();
    }
    public static CreateFundingItemRequestDto of(String fundingName, String fundDetail, BigDecimal itemPrice, BigDecimal goalPrice, LocalDate startDate, LocalDate finishDate){
        return new CreateFundingItemRequestDto(fundingName, fundDetail, itemPrice, goalPrice, startDate, finishDate);
    }
}
