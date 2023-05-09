package com.habday.server.dto.res.fund;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import org.springframework.http.ResponseEntity;

public class CreateFundingItemResponse extends BaseResponse {
    private CreateFundingItemResponse(Boolean success, String msg) {
        super(success, msg);
    }

    public static ResponseEntity<CreateFundingItemResponse> newResponse(SuccessCode code) {
        return new ResponseEntity(CreateFundingItemResponse.of(true, code.getMsg()), code.getStatus());
    }

}
