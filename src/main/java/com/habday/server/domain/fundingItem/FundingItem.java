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

    @Column
    private LocalDate finishDate;

    @Column
    private int percentage;//totalPrice/goalPrice하면 돼서 필요 없을수도

    @Column
    @Enumerated(value = EnumType.STRING)
    private FundingState status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;
}
