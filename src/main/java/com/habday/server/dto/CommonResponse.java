package com.habday.server.dto;

import com.habday.server.constants.code.SuccessCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
@Getter
public class CommonResponse<T> extends BaseResponse {
    T data;

    public CommonResponse(Boolean success, String msg, T data){
        super(success, msg);
        this.data = data;
    }

    public static <T> ResponseEntity<CommonResponse> toResponse(SuccessCode code, T data){
        CommonResponse response = new CommonResponse(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }

}
