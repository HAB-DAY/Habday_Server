package com.habday.server.dto.req.fund;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.habday.server.constants.FundingState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.habday.server.constants.FundingState.PROGRESS;

@Getter
@NoArgsConstructor
public class CreateFundingItemRequestDto {

    private String fundingItemImg;
    private String fundingName;
    private String fundDetail;
    private int itemPrice;
    private int totalPrice;
    private int goalPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @Builder
    public CreateFundingItemRequestDto(String fundingItemImg, String fundingName, String fundDetail, int itemPrice, int totalPrice, int goalPrice, LocalDate startDate, LocalDate finishDate) {
        this.fundingItemImg = fundingItemImg;
        this.fundingName = fundingName;
        this.fundDetail = fundDetail;
        this.itemPrice = itemPrice;
        this.totalPrice = totalPrice;
        this.goalPrice = goalPrice;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public FundingItem toCreateFundingItem(String fundingItemImg, String fundingName, String fundDetail, int itemPrice, int totalPrice, int goalPrice, LocalDate startDate, LocalDate finishDate, Member member) {
        return FundingItem.builder()
                .fundingItemImg(fundingItemImg)
                .fundingName(fundingName)
                .fundDetail(fundDetail)
                .itemPrice(itemPrice)
                .totalPrice(totalPrice)
                .goalPrice(goalPrice)
                .startDate(startDate)
                .finishDate(finishDate)
                .status(PROGRESS)
                .member(member)
                .build();
    }

    /*public static CreateFundingItemRequestDto of(String fundingItemImg, String fundingName, String fundDetail, int itemPrice, int totalPrice, int goalPrice, LocalDate startDate, LocalDate finishDate){
        return new CreateFundingItemRequestDto(fundingItemImg, fundingName, fundDetail, itemPrice, totalPrice, goalPrice, startDate, finishDate);
    }
     */
}
