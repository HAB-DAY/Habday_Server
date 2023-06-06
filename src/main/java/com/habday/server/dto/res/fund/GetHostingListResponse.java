package com.habday.server.dto.res.fund;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class GetHostingListResponse extends BaseResponse {
    private GetHostingListResponseDto data;

    public GetHostingListResponse(Boolean success, String msg, GetHostingListResponseDto data){
        super(success, msg);
        this.data = data;
    }

    public static ResponseEntity<GetHostingListResponse> newResponse(SuccessCode code, GetHostingListResponseDto data){
        GetHostingListResponse response = new GetHostingListResponse(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }

}
