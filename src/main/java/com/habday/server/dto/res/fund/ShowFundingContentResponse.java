package com.habday.server.dto.res.fund;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class ShowFundingContentResponse extends BaseResponse {
    ShowFundingContentResponseDto data;

    private ShowFundingContentResponse(Boolean success, String msg, ShowFundingContentResponseDto data){
        super(success, msg);
        this.data = data;
    }

    public static ResponseEntity<ShowFundingContentResponse> newResponse(SuccessCode code, ShowFundingContentResponseDto data){
        ShowFundingContentResponse response = new ShowFundingContentResponse(true, code.getMsg(), data);
        return new ResponseEntity(response, code.getStatus());
    }
}
