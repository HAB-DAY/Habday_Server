package com.habday.server.domain.fundingMember;

import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDate fundingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundingItemId")
    private FundingItem fundingItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundingMemberId")
    private Member member;
}
