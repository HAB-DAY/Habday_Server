package com.habday.server.dto.res;

import com.habday.server.constants.code.SuccessCode;
import com.habday.server.dto.BaseResponse;
import org.springframework.http.ResponseEntity;

public class DeleteFundingItemResponse extends BaseResponse {
    private DeleteFundingItemResponse(Boolean success, String msg) {
        super(success, msg);
    }

    public static ResponseEntity<DeleteFundingItemResponse> newResponse(SuccessCode code) {
        return new ResponseEntity(DeleteFundingItemResponse.of(true, code.getMsg()), code.getStatus());
    }
}
