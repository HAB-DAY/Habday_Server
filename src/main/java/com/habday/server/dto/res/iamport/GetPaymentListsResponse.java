package com.habday.server.dto.res.iamport;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class GetPaymentListsResponse extends BaseResponse {
    GetPaymentListsResponseDto data;

    private GetPaymentListsResponse(Boolean success, String msg, GetPaymentListsResponseDto data){
        super(success, msg);
        this.data = data;
    }

    public static GetPaymentListsResponse of(Boolean success, String msg, GetPaymentListsResponseDto data){
        return new GetPaymentListsResponse(success, msg, data);
    }

    public static ResponseEntity<GetPaymentListsResponse> newResponse(SuccessCode code, GetPaymentListsResponseDto data){
        GetPaymentListsResponse response = GetPaymentListsResponse.of(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }


}
