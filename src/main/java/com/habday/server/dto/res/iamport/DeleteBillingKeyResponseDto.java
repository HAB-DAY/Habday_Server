package com.habday.server.dto.res.iamport;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteBillingKeyResponseDto {
    Boolean isDelete;
    Long paymentId;
    String paymentName;
    String cardNumberEnd;
    //builder패턴을 사용하면 값이 빠져도 잡아주지 않지만 Of를 사용하면 값이 빠지면 잡아준다?!
    //object 형태의 데이터를 파라미터로 넘겨주거나 다른 데이터를 같이 넘겨주면 그대로 빌더패턴을 적용할 수 있음
    //but! builder 패턴의 장점을 활용하기 어려울 수 있음

    @Builder
    public DeleteBillingKeyResponseDto(Boolean isDelete, Long paymentId, String paymentName, String cardNumberEnd){
        this.isDelete = isDelete;
        this.paymentId = paymentId;
        this.paymentName = paymentName;
        this.cardNumberEnd = cardNumberEnd;
    }
}
