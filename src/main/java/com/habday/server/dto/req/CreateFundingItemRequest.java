package com.habday.server.dto.req;

import com.habday.server.constants.FundingState;
import com.habday.server.domain.member.Member;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateFundingItemRequest {
    private FundingState status;
    private Member member;

    public CreateFundingItemRequestDto toSaveFundingItem(String fundingItemImg, String fundingName, String fundDetail, int itemPrice, int totalPrice, int goalPrice, LocalDate startDate, LocalDate finishDate) {
        return CreateFundingItemRequestDto.of(member.getId(), fundingItemImg, fundingName, fundDetail, itemPrice, totalPrice, goalPrice, startDate, finishDate, status);
    }
}
