package com.habday.server.domain.fundingItem;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.habday.server.constants.state.FundingConfirmState;
import com.habday.server.constants.state.FundingState;
import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@DynamicInsert
@Getter
@NoArgsConstructor
@Entity
@Table(name = "FUNDING_ITEM")
public class FundingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fundingItemId")
    private Long id;

    @Column
    private String fundingItemImg;

    @Column
    private String fundingName;

    @Column
    private String fundDetail;

    @Column
    private BigDecimal itemPrice;

    @ColumnDefault("0")
    private BigDecimal totalPrice;

    @Column
    private BigDecimal goalPrice;
    
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @Column
    private int percentage;//totalPrice/goalPrice하면 돼서 필요 없을수도

    /*DONE 추가: 이미 만료 처리가 완료된 것은 걸러내기 위해서*/
    @ColumnDefault(value = "'FALSE'")
    @Enumerated(value = EnumType.STRING)
    private FundingConfirmState isConfirm;

    @Column
    @Enumerated(value = EnumType.STRING)
    private FundingState status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public FundingItem(String fundingItemImg, String fundingName, String fundDetail, BigDecimal itemPrice, BigDecimal totalPrice, BigDecimal goalPrice, LocalDate startDate, LocalDate finishDate, FundingState status, Member member) {
        this.fundingItemImg = fundingItemImg;
        this.fundingName = fundingName;
        this.fundDetail = fundDetail;
        this.itemPrice = itemPrice;
        this.totalPrice = totalPrice;
        this.goalPrice = goalPrice;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
        this.member = member;
    }

    public FundingItem updatePricePercentage(BigDecimal totalPrice, int percentage){
        this.totalPrice = totalPrice;
        this.percentage = percentage;
        return this;
    }

    public FundingItem updateFundingSuccess(){
        this.status = FundingState.SUCCESS;
        return this;
    }

    public FundingItem updateFundingFail(){
        this.status = FundingState.FAIL;
        return this;
    }

    public void updateIsConfirmTrue() {
        this.isConfirm = FundingConfirmState.TRUE;
    }
    public void updateIsConfirmDone() {
        this.isConfirm = FundingConfirmState.DONE;
    }

    // 펀딩 수정
    public void updateFundingItemImg(String fundingItemImg) {
        this.fundingItemImg = fundingItemImg;
    }
    public void updateFundingItemName(String fundingName) {
        this.fundingName = fundingName;
    }
    public void updateFundDetail(String fundDetail) {
        this.fundDetail = fundDetail;
    }

    public void updateCancel(BigDecimal totalPrice, int percentage){
        this.totalPrice = totalPrice;
        this.percentage = percentage;
    }
}
