package com.habday.server.dto.res.fund;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class ParticipateFundingResponse extends BaseResponse {
    ParticipateFundingResponseDto data;

    private ParticipateFundingResponse(Boolean success, String msg, ParticipateFundingResponseDto data){
        super(success, msg);
        this.data = data;
    }

    private ParticipateFundingResponse of(Boolean success, String msg, ParticipateFundingResponseDto data){
        return new ParticipateFundingResponse(success, msg, data);
    }

    public static ResponseEntity<ParticipateFundingResponse> newResponse(SuccessCode code, ParticipateFundingResponseDto data){
        ParticipateFundingResponse response = new ParticipateFundingResponse(true, code.getMsg(), data);//of(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }
}
