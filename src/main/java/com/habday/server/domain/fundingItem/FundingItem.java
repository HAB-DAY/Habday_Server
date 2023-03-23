package com.habday.server.domain.fundingItem;


import com.habday.server.constants.FundingState;
import com.habday.server.domain.member.Member;
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

    @Column(nullable = false)
    private String fundingItemImg;

    @Column(nullable = false)
    private String fundingName;

    @Column(nullable = false)
    private String fundDetail;

    @Column(nullable = false)
    private int itemPrice;//링크 첨부?

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private int goalPrice;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate finishDate;

    @Column(nullable = false)
    private int percentage;//totalPrice/goalPrice하면 돼서 필요 없을수도

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FundingState status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;
}
