package com.habday.server.dto.res.fund;

import com.habday.server.constants.FundingState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class GetParticipatedListResponseDto {
    private FailList failList;
    private SuccessList successList;
    private ProgressList progressList;
    @Getter
    public static class FailList{
        private Long id; //FundingItem
        private String fundingItemImg; //FundingItem
        private String fundingName; //FundingItem
        private BigDecimal amount; //FundingMember
        private LocalDate startDate; //FundingItem
        private LocalDate finishDate; //FundingItem
        private FundingState status; //FundingItem
    }

    @Getter
    public static class SuccessList{
        private Long fundingItemId;
        private String fundingItemImg;
        private String fundingName;
        private BigDecimal amount; //fundingMember에서
        private LocalDate startDate;
        private LocalDate finishDate;
        private FundingState status;
    }

    @Getter
    public static class ProgressList{
        private Long fundingItemId;
        private String fundingItemImg;
        private String fundingName;
        private BigDecimal amount; //fundingMember에서
        private LocalDate startDate;
        private LocalDate finishDate;
        private FundingState status;
    }
}
