package com.habday.server.dto.req;

import com.habday.server.constants.FundingState;
import com.habday.server.domain.member.Member;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateFundingItemRequest {
    private FundingState status;
    private Member member;

    public CreateFundingItemRequestDto toSaveFundingItem(String fundingName, String fundDetail, BigDecimal itemPrice, BigDecimal totalPrice, BigDecimal goalPrice, LocalDate startDate, LocalDate finishDate) {
        return CreateFundingItemRequestDto.of(fundingName, fundDetail, itemPrice, totalPrice, goalPrice, startDate, finishDate);
    }
}
