package com.habday.server.dto.res.fund;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class GetParticipatedListResponse extends BaseResponse {
    private GetParticipatedListResponseDto data;

    private GetParticipatedListResponse(Boolean success, String msg, GetParticipatedListResponseDto data){
        super(success, msg);
        this.data = data;
    }

    public static ResponseEntity<GetParticipatedListResponse> newResponse(SuccessCode code, GetParticipatedListResponseDto data){
        GetParticipatedListResponse response = new GetParticipatedListResponse(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }
}
