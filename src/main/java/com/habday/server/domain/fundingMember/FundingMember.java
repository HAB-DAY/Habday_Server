package com.habday.server.domain.fundingMember;

import com.habday.server.constants.FundingState;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "FUNDING_MEMBER")
public class FundingMember{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fundingMemberId")
    private Long id;

    @Column
    private String name;

    @Column
    private BigDecimal amount;

    @Column
    private String message;

    @Column
    private LocalDate fundingDate;

    @Column
    private Long paymentId;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ScheduledPayState payment_status;

    @Column
    private String merchant_id;

    @Column
    private String imp_uid;

    @Column
    private BigDecimal cancel_amount;

    @Column
    private String reason;

    @Column
    private String fail_reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundingItemId")
    private FundingItem fundingItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public FundingMember(String name, BigDecimal amount, String message, LocalDate fundingDate, Long paymentId, FundingItem fundingItem, Member member){
        this.name = name;
        this.amount = amount;
        this.message = message;
        this.fundingDate = fundingDate;
        this.paymentId = paymentId;
        this.fundingItem = fundingItem;
        this.member = member;
    }

}
