package com.habday.server.dto.res.iamport;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetPaymentListsResponseDto {
    private List<PaymentList> payments;

    private GetPaymentListsResponseDto(List<PaymentList> payments){
        this.payments = payments;
    }

    public static GetPaymentListsResponseDto of(List<PaymentList> payments){
        return new GetPaymentListsResponseDto(payments);
    }

    @Getter
    public static class PaymentList{
        private Long paymentId;
        private String paymentName;//paymentId/paymentName(결제할 때는 paymentId만 주는거지

        public PaymentList(Long id, String paymentName){
            this.paymentId = id;
            this.paymentName = paymentName;
        }
    }
}
