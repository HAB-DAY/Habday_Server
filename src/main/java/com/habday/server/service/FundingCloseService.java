package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Common;
import com.habday.server.config.email.EmailFormats;
import com.habday.server.constants.CustomException;
import com.habday.server.constants.code.ExceptionCode;
import com.habday.server.constants.state.FundingState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.dto.req.iamport.CallbackScheduleRequestDto;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.habday.server.constants.state.ScheduledPayState.fail;
import static com.habday.server.constants.state.ScheduledPayState.paid;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingCloseService extends Common {
    private final IamportService iamportService;
    private final EmailFormats emailFormats;
    private final PayService payService;

    /*
     * 1. FundingItem status == PROGRESS 중 오늘 날짜랑 같은게 있는지 확인하기(fundingService.checkFundingFinishDate()
     * 2. 날짜가 같으면 성공 실패(목표 퍼센트 달성) 확인하기(fundingService.checkFundingGoalPercent())
     * 3. 퍼센트 달성 실패 시 예약 취소하기
     *   - fundingItem status fail로 업데이트
     *   - fundingMember status cancel로 업데이트
     *   - 펀딩 실패 메일 보내기
     * 4. 퍼센트 달성 성공 시 fundingItem status success로 업데이트
     *   - 펀딩 성공 메일 보내기
     * */
    @Transactional
    public void checkFundingSuccess(FundingItem fundingItem) {//성공한 아이템이 들어오게
        BigDecimal goalPercent = fundingItem.getGoalPrice().divide(fundingItem.getItemPrice(), BigDecimal.ROUND_DOWN); //최소목표퍼센트
        BigDecimal realPercent = fundingItem.getTotalPrice().divide(fundingItem.getItemPrice(), BigDecimal.ROUND_DOWN); // 실제달성퍼센트

        log.info("itemId: " + fundingItem.getId() + " goalPercent^^ " + goalPercent + " realPercent^^ " + realPercent);
        log.info("itemId: " +fundingItem.getId() + " realPercent.compareTo(goalPercent)^^ : " + realPercent.compareTo(goalPercent));

        if (realPercent.compareTo(goalPercent) == 0 ||  realPercent.compareTo(goalPercent) == 1) { // 펀딩 최소 목표 퍼센트에 달성함
            fundingItem.updateFundingSuccess();
            log.info("최소 목표퍼센트 이상 달성함");
            emailFormats.sendFundingSuccessEmail(fundingItem);
        } else { // 펀딩 최소 목표 퍼센트에 달성 못함
            log.info("최소 목표퍼센트 이상 달성 실패");
            fundingItem.updateFundingFail();
            payService.unscheduleAll(fundingItem);
            emailFormats.sendFundingFailEmail(fundingItem);//throw new CustomException(FAIL_FINISH_FUNDING);
        }
    }

    @Transactional
    public void fundingSuccess(FundingItem fundingItem){
        log.info("최소 목표퍼센트 이상 달성함");
        emailFormats.sendFundingSuccessEmail(fundingItem);
    }

    @Transactional
    public void fundingFail(FundingItem fundingItem){
        log.info("최소 목표퍼센트 이상 달성 실패");
        fundingItem.updateFundingFail();
        payService.unscheduleAll(fundingItem);
        emailFormats.sendFundingFailEmail(fundingItem);//throw new CustomException(FAIL_FINISH_FUNDING);
    }


    /*
        <웹훅>
     * 1. 12시반  예약결제 실행
     * 2. 웹훅에서 fundingMember.payment_status paid로 update
     * 3. 결제 실패 시 실패 메일과 수동 결제 링크 보내기
     * 4. 결제 성공 시 결제 성공 메일 보내기
     * */
    @Transactional
    public void callbackSchedule(CallbackScheduleRequestDto callbackRequestDto, HttpServletRequest request){
        log.info("callbackSchedule: " + new Gson().toJson(callbackRequestDto));
        String clientIp = getIp(request);
        String[] ips = {"52.78.100.19", "52.78.48.223", "52.78.5.241"};
        List<String> ipLists = new ArrayList<>(Arrays.asList(ips));

        Optional<FundingMember> fundingMember = Optional.ofNullable(fundingMemberRepository.findByMerchantId(callbackRequestDto.getMerchant_uid()));
        FundingItem fundingItem = fundingMember.orElseThrow(()->
            new CustomException(ExceptionCode.NO_FUNDING_MEMBER_ID)).getFundingItem();
        BigDecimal amount = fundingMember.get().getAmount();

        IamportResponse<Payment> response = iamportService.paymentByImpUid(callbackRequestDto.getImp_uid());
        log.info("response: " + new Gson().toJson(response));

        if (!ipLists.contains(clientIp)){
            fundingMember.get().updateWebhookFail("ip주소가 맞지 않음");
            log.info("callbackSchedule() ip 주소 안맞음");
            return;//throw new CustomException(UNAUTHORIZED_IP);
            //exception 날리면 트랜잭션이 롤백되어버려 영속성컨텍스트 flush 안됨
        }

        if(amount.compareTo(response.getResponse().getAmount()) !=0){
            fundingMember.get().updateWebhookFail("결제 금액이 맞지 않음");
            log.info("callbackSchedule(): member-amount : " + amount);
            log.info("callbackSchedule() 결제 금액 안맞음 " + response.getResponse().getMerchantUid());
            return;//throw new CustomException(NO_CORRESPONDING_AMOUNT);
        }
        String[] receiver = {fundingMember.get().getMember().getEmail()};

        if(callbackRequestDto.getStatus().equals(paid.getMsg())){
            fundingMember.get().updateWebhookSuccess();
            log.info("callbackSchedule() paid로 update" + response.getResponse().getMerchantUid());
            emailFormats.sendPaymentSuccessEmail(fundingItem, receiver, amount);
        }else{
            fundingMember.get().updateWebhookFail(response.getResponse().getFailReason());
            log.info("callbackSchedule() fail로 update" + response.getResponse().getMerchantUid());
            emailFormats.sendPaymentFailEmail(fundingItem, receiver);
        }
    }


    private String getIp(HttpServletRequest request) {
        String [] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", };

        String ip = request.getHeader("X-Forwarded-For");
        for(String header: headers){
            if (ip == null){
                ip = request.getHeader(header);
                log.info(">>>> " + header + " : " + ip);
            }
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /*현재 사용하지 않아 주석처리 해놓음*/
    // 펀딩 기간 만료 후, 펀딩 목표 퍼센트 달성 했는지 여부 확인 로직
//    public void checkFundingFinishDate(Long fundingItemId){
//        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
//                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
//
//        LocalDate now = LocalDate.now(); //현재 날짜 구하기
//        System.out.println("now^^ " + now.isEqual(fundingItem.getFinishDate()));
//
//        if (now.compareTo(fundingItem.getFinishDate()) == 0){
//            checkFundingGoalPercent(fundingItem);
//        } else if(now.compareTo(fundingItem.getFinishDate()) < 0){
//            log.info("종료 이전");
//            throw new CustomException(NOT_FINISH_FUNDING); // 펀딩이 아직 종료되지 않음
//        }else{
//            log.info("종료 이후");
//            throw new CustomException(ALREADY_FINISHED_FUNDING);
//        }
//    }

}