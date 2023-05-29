package com.habday.server.dto.req.iamport;

import lombok.Getter;

@Getter
public class ShowSchedulesRequestDto {
    private int s_year;
    private int s_month;
    private int s_date;
    private int e_year;
    private int e_month;
    private int e_date;
    private String schedule_status;
    private int page;
}
