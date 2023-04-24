package com.habday.server.dto.req.iamport;

import lombok.Getter;

@Getter
public class CallbackScheduleRequestDto {
    private String imp_uid;
    private String merchant_uid;
    private String status;

    public String printRequest(){
        return "imp_uid: " + imp_uid + " merchant_uid: " + merchant_uid + " status: " + status;
    }
}
