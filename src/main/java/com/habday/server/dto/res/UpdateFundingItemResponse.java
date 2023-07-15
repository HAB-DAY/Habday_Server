package com.habday.server.dto.res;

import com.habday.server.constants.code.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class UpdateFundingItemResponse extends BaseResponse {
    private UpdateFundingItemResponse(Boolean success, String msg) { super(success, msg); }

    public static ResponseEntity<UpdateFundingItemResponse> newResponse(SuccessCode code) {
        return new ResponseEntity(UpdateFundingItemResponse.of(true, code.getMsg()), code.getStatus());
    }
}
