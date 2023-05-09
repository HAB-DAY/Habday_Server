package com.habday.server.domain.payment;

import com.habday.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "PAYMENT")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentId")
    private Long id;

    @Column(nullable = false)
    private String paymentName;

    @Column(nullable = false)
    private String billingKey;

    @Column()
    private String cardNumberEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public Payment(String paymentName, String billingKey, Member member, String cardNumberEnd){
        this.paymentName = paymentName;
        this.billingKey = billingKey;
        this.member = member;
        this.cardNumberEnd = cardNumberEnd;
    }
}
