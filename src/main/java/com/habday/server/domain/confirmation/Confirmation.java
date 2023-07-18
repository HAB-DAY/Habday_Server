package com.habday.server.domain.confirmation;

import com.habday.server.domain.BaseTimeEntity;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import com.habday.server.dto.req.fund.ConfirmationRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Confirmation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirmationId")
    private Long id;

    @Column
    private String title;

    @Column
    private String confirmationImg;

    @Column
    private String message;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundingItemId")
    private FundingItem fundingItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public Confirmation(String confirmationImg, ConfirmationRequest request, FundingItem fundingItem, Member member){
        this.confirmationImg = confirmationImg;
        this.title = request.getTitle();
        this.message = request.getMessage();
        this.fundingItem = fundingItem;
        this.member = member;
    }

}
