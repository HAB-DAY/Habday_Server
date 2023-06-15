package com.habday.server.dto.res.iamport;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
@Getter
public class DeleteBillingKeyResponse extends BaseResponse {
    DeleteBillingKeyResponseDto data;

    public DeleteBillingKeyResponse(Boolean success, String msg, DeleteBillingKeyResponseDto data){
        super(success, msg);
        this.data = data;
    }

    public static ResponseEntity<DeleteBillingKeyResponse> toResponse(SuccessCode code, DeleteBillingKeyResponseDto data){
        DeleteBillingKeyResponse response = new DeleteBillingKeyResponse(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }

}
