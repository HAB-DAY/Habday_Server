package com.habday.server.dto.res;

import com.habday.server.constants.code.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class MemberProfileResponse extends BaseResponse {

    private MemberProfileResponse(Boolean success, String msg) {
        super(success, msg);
    }

    public static ResponseEntity<MemberProfileResponse> newResponse(SuccessCode code){
        return new  ResponseEntity(MemberProfileResponse.of(true, code.getMsg()), code.getStatus());
    }
}
