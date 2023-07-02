package com.habday.server.dto.res.fund;

import com.habday.server.classes.implemented.HostedList.HostedListDto;
import com.habday.server.constants.FundingState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetHostingListResponseDto<T> {
    private List<T> hostingLists;
    private Boolean hasNext;

    public GetHostingListResponseDto(List<T> hostingLists, Boolean hasNext){
        this.hostingLists = hostingLists;
        this.hasNext = hasNext;
    }
}
