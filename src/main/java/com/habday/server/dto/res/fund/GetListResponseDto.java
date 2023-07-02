package com.habday.server.dto.res.fund;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetListResponseDto<T> {
    private List<T> hostingLists;
    private Boolean hasNext;

    public GetListResponseDto(List<T> hostingLists, Boolean hasNext){
        this.hostingLists = hostingLists;
        this.hasNext = hasNext;
    }
}
