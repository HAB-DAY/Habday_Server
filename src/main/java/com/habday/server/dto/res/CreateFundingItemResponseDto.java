package com.habday.server.dto.res;

import com.habday.server.constants.SuccessCode;
import com.habday.server.dto.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class CreateFundingItemResponseDto extends BaseResponse {

    private Long memberId; // 펀딩 생성 멤버 Id
    private Long fundingItemId; // 펀딩 아이템 Id
    private String fundingDetailUrl = "http://13.124.209.40:8080/funding/showFundingContent?itemId={}";
    private CreateFundingItemResponseDto(Boolean success, String msg) {
        super(success, msg);
    }




    public static ResponseEntity<CreateFundingItemResponseDto> newResponse(SuccessCode code) {
        return new ResponseEntity(CreateFundingItemResponseDto.of(true, code.getMsg()), code.getStatus());
    }

}
