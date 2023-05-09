package com.habday.server.dto.req.fund;

import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ParticipateFundingRequest {
    Long fundingItemId;
    String name;
    String message;
    LocalDate fundingDate;
    BigDecimal amount;
    Long paymentId;

    NoneAuthPayScheduleRequestDto scheduleData;

    public void of(){

    }
}
