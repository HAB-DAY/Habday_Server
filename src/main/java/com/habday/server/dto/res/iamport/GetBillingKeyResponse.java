package com.habday.server.dto.res.iamport;

import com.google.gson.Gson;
import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class GetBillingKeyResponse extends BaseResponse {
    GetBillingKeyResponseDto data;

    private GetBillingKeyResponse(Boolean success, String msg, GetBillingKeyResponseDto data){
        super(success, msg);
        this.data = data;
    }

    public static ResponseEntity<GetBillingKeyResponse> toResponse(SuccessCode code, GetBillingKeyResponseDto data){
        GetBillingKeyResponse response = new GetBillingKeyResponse(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }
}
