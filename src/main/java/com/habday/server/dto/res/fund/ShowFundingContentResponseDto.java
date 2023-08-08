package com.habday.server.dto.res.fund;

import com.habday.server.constants.state.FundingState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ShowFundingContentResponseDto {
    private String fundingItemImg;
    private String fundingName;
    private String fundDetail;
    private BigDecimal itemPrice; //상품 가격
    private BigDecimal totalPrice; //누적 가격
    private BigDecimal goalPrice; // 목표 금액
    private LocalDate startDate;
    private LocalDate finishDate;
    private int percentage;
    private FundingState status;
    private String hostName;
    private List<FundingParticipantList> fundingParticipantList;
    private Boolean isConfirmation;
    private String showDetailUrl;

    //생성자에는 static을 사용할 수 없다.
    //of를 static으로 놓고 builder 패턴을 적용해 객체를 생성해서 반환하려 해도 결국은 생성자가 필요하다.
    //따라서 생성자 따로 만들고, of 함수를 static으로 놔서 전역처럼 사용하는 것.
    @Builder
    private ShowFundingContentResponseDto(String fundingItemImg, String fundingName, String fundDetail, BigDecimal itemPrice,
               BigDecimal totalPrice, BigDecimal goalPrice, LocalDate startDate, LocalDate finishDate, int percentage,
               FundingState status, String hostName, List<FundingParticipantList> fundingParticipantList,
                                          Boolean isConfirmation, String showDetailUrl){
        this.fundingItemImg = fundingItemImg;
        this.fundingName = fundingName;
        this.fundDetail = fundDetail;
        this.itemPrice = itemPrice;
        this.totalPrice = totalPrice;
        this.goalPrice = goalPrice;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.percentage = percentage;
        this.status = status;
        this.hostName = hostName;
        this.fundingParticipantList = fundingParticipantList;
        this.isConfirmation = isConfirmation;
        this.showDetailUrl = showDetailUrl;
    }


    public static ShowFundingContentResponseDto of(FundingItem fundingItem, Member member, List<FundingParticipantList> fundingParticipantList, Boolean isConfirmation, String showDetailUrl){
        return ShowFundingContentResponseDto.builder()
                .fundingItemImg(fundingItem.getFundingItemImg())
                .fundingName(fundingItem.getFundingName())
                .fundDetail(fundingItem.getFundDetail())
                .itemPrice(fundingItem.getItemPrice())
                .totalPrice(fundingItem.getTotalPrice())
                .goalPrice(fundingItem.getGoalPrice())
                .startDate(fundingItem.getStartDate())
                .finishDate(fundingItem.getFinishDate())
                .percentage(fundingItem.getPercentage())
                .status(fundingItem.getStatus())
                .hostName(member.getName())
                .fundingParticipantList(fundingParticipantList)
                .isConfirmation(isConfirmation)
                .showDetailUrl(showDetailUrl)
                .build();
    }

    @Getter
    public static class FundingParticipantList{
        private String name;
        private LocalDate fundingDate;
        private BigDecimal amount;
        private String message;

        public FundingParticipantList(String name, LocalDate fundingDate, BigDecimal amount, String message){
            this.name = name;
            this.fundingDate = fundingDate;
            this.amount = amount;
            this.message = message;
        }
    }
}
