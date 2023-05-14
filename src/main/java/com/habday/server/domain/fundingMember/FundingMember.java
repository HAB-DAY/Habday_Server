package com.habday.server.domain.fundingMember;

import com.habday.server.constants.FundingState;
import com.habday.server.constants.ScheduledPayState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@DynamicInsert
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

    @ColumnDefault("0")
    private BigDecimal cancel_amount;

    @Column
    private String reason;

    @Column
    private LocalDate cancelDate;

    @Column
    private String fail_reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundingItemId")
    private FundingItem fundingItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public FundingMember(String name, BigDecimal amount, String message, LocalDate fundingDate, Long paymentId,
                         String merchant_id, String imp_uid, FundingItem fundingItem, Member member,
                         ScheduledPayState payment_status){
        this.name = name;
        this.amount = amount;
        this.message = message;
        this.fundingDate = fundingDate;
        this.paymentId = paymentId;
        this.payment_status = payment_status;
        this.merchant_id = merchant_id;
        this.imp_uid = imp_uid;
        this.fundingItem = fundingItem;
        this.member = member;
    }

    public FundingMember updateCancel(BigDecimal cancel_amount, String reason, ScheduledPayState payment_status, LocalDate cancelDate){
        this.cancel_amount = cancel_amount;
        this.reason = reason;
        this.payment_status = payment_status;
        this.cancelDate = cancelDate;
        return this;
    }

}
