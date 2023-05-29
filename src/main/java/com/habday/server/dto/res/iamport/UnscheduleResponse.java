package com.habday.server.dto.res.iamport;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class UnscheduleResponse extends BaseResponse {
    UnscheduleResponseDto data;

    private UnscheduleResponse(Boolean success, String msg, UnscheduleResponseDto data){
        super(success, msg);
        this.data = data;
    }

    public static ResponseEntity<UnscheduleResponse> newResponse(SuccessCode code, UnscheduleResponseDto data){
        UnscheduleResponse response = new UnscheduleResponse(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }
}
