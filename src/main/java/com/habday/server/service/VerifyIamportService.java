package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.constants.ExceptionCode;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.Payment;
import com.habday.server.domain.payment.PaymentRepository;
import com.habday.server.dto.req.iamport.NoneAuthPayBillingKeyRequest;
import com.habday.server.dto.res.iamport.GetBillingKeyResponseDto;
import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto.PaymentList;
import com.habday.server.dto.res.iamport.GetPaymentListsResponseDto;
import com.habday.server.exception.CustomException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.BillingCustomerData;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.List;

import static com.habday.server.constants.ExceptionCode.BILLING_KEY_SAVE_FAIL;
import static com.habday.server.constants.ExceptionCode.NO_MEMBER_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyIamportService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final IamportClient iamportClient =
            new IamportClient("3353771108105637", "CrjUGS59xKtdBK1eYdj7r4n5TnuEDGcQo12NLdRCetjCUCnMsDFk5Q9IqOlhhH7QELBdakQTIB5WfPcg");

    //todo 사용자 billingkey 카드 정보 리스트로 가져오기

    @Transactional
    public GetBillingKeyResponseDto getBillingKey(NoneAuthPayBillingKeyRequest billingKeyRequest, Long memberId) throws IamportResponseException, IOException {
        //todo 이미 있는 결제정보인지 확인하기(빌링키 조회한 카드번호 비교
        BillingCustomerData billingCustomerData = new BillingCustomerData(
                billingKeyRequest.getCustomer_uid(), billingKeyRequest.getCard_number(),
                billingKeyRequest.getExpiry(), billingKeyRequest.getBirth());

        billingCustomerData.setPwd2Digit(billingKeyRequest.getPwd_2digit());
        billingCustomerData.setPg("nice.nictest04m");

        //todo 시점 주의
        BillingCustomer billingCustomer= iamportClient.postBillingCustomer(billingKeyRequest.getCustomer_uid(), billingCustomerData).getResponse();

        //todo memberid는 jwt에서 가져옴
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        try{
            paymentRepository.save(Payment.builder()
                    .paymentName(billingKeyRequest.getPayment_name())
                    .billingKey(billingKeyRequest.getCustomer_uid())
                    .member(member)
                    .build());
        }catch(Exception e){
            throw new CustomException(BILLING_KEY_SAVE_FAIL);
        }

        //todo 저장 예외
        return GetBillingKeyResponseDto.of(billingKeyRequest.getPayment_name(), billingCustomer == null ? "" : billingCustomer.getCustomerUid());
    }//todo 오류 처리 하기 billingCustomer이 null이면 안됨!!

    public GetPaymentListsResponseDto getPaymentLists(Long memberId){
        List<PaymentList> paymentLists =  paymentRepository.findByMemberId(memberId);
        if (paymentLists == null){
            return GetPaymentListsResponseDto.of(null);//return 등록된 결제정보 없음
        }

        return GetPaymentListsResponseDto.of(paymentLists);
    }
}
