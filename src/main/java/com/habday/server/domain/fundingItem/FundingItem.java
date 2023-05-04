package com.habday.server.domain.fundingItem;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.habday.server.constants.FundingState;
import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

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
    private int itemPrice;//링크 첨부?

    @Column
    private int totalPrice;

    @Column
    private int goalPrice;

    @Column
    private LocalDate startDate;
    
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @Column
    private int percentage;//totalPrice/goalPrice하면 돼서 필요 없을수도

    @Column
    @Enumerated(value = EnumType.STRING)
    private FundingState status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public FundingItem(String fundingItemImg, String fundingName, String fundDetail, int itemPrice, int totalPrice, int goalPrice, LocalDate startDate, LocalDate finishDate, FundingState status, Member member) {
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

}
