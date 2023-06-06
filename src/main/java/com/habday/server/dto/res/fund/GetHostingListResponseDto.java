package com.habday.server.dto.res.fund;

import com.habday.server.constants.FundingState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class GetHostingListResponseDto {
    private Long fundingItemId; //FundingItem
    private String fundingItemImg; //FundingItem
    private String fundingName; //FundingItem
    private BigDecimal amount; //FundingMember
    private LocalDate startDate; //FundingItem
    private LocalDate finishDate; //FundingItem
    private FundingState status; //FundingItem
}
