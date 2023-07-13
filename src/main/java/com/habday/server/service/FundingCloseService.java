package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Calculation;
import com.habday.server.classes.Common;
import com.habday.server.config.email.EmailMessage;
import com.habday.server.config.email.EmailService;
import com.habday.server.config.retrofit.RestInterface;
import com.habday.server.constants.CmnConst;
import com.habday.server.constants.state.FundingState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.dto.req.iamport.CallbackScheduleRequestDto;
import com.habday.server.dto.req.iamport.NoneAuthPayUnscheduleRequestDto;
import com.habday.server.dto.res.iamport.UnscheduleResponseDto;
import com.habday.server.exception.CustomException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.habday.server.constants.CmnConst.scheduleCron;
import static com.habday.server.constants.code.ExceptionCode.*;
import static com.habday.server.constants.state.ScheduledPayState.fail;
import static com.habday.server.constants.state.ScheduledPayState.paid;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingCloseService extends Common {
    private final IamportService iamportService;
    private final RestInterface restService;
    private final Calculation calculation;
    private final EmailService emailService;

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
    @Scheduled(cron = scheduleCron) // "0 5 0 * * *" 매일 밤 0시 5분에 실행
    public void checkFundingState() {
        log.info("schedule 시작");
        List<FundingItem> overdatedFundings =  fundingItemRepository.findByStatusAndFinishDate(FundingState.PROGRESS, LocalDate.now());
        overdatedFundings.forEach(fundingItem -> {
            log.info("오늘 마감 fundingItem: " + fundingItem.getId());
            checkFundingGoalPercent(fundingItem);
        });
        log.info("schedule 끝");
    }
    @Transactional
    public void checkFundingGoalPercent(FundingItem fundingItem) {
        BigDecimal goalPercent = fundingItem.getGoalPrice().divide(fundingItem.getItemPrice(), BigDecimal.ROUND_DOWN); //최소목표퍼센트
        BigDecimal realPercent = fundingItem.getTotalPrice().divide(fundingItem.getItemPrice(), BigDecimal.ROUND_DOWN); // 실제달성퍼센트

        log.info("itemId: " + fundingItem.getId() + "goalPercent^^ " + goalPercent);
        log.info("itemId: " + fundingItem.getId() + "realPercent^^ " + realPercent);
        log.info("itemId: " +fundingItem.getId() + "realPercent.compareTo(goalPercent)^^ : " + realPercent.compareTo(goalPercent));

        if (realPercent.compareTo(goalPercent) == 0 ||  realPercent.compareTo(goalPercent) == 1) { // 펀딩 최소 목표 퍼센트에 달성함
            fundingItem.updateFundingState(FundingState.SUCCESS);
            log.info("최소 목표퍼센트 이상 달성함");

            EmailMessage emailMessage = EmailMessage.builder()
                    .to(getReceiverList(fundingItem))
                    .subject("HABDAY" + "펀딩 성공 알림" )
                    .message("'" + fundingItem.getFundingName()+"' 펀딩이 성공했습니다. \n" +
                            "00시 " + CmnConst.paymentDelayMin + "분에 결제 처리될 예정입니다.")
                    .build();
            emailService.sendEmail(emailMessage);
        } else { // 펀딩 최소 목표 퍼센트에 달성 못함
            log.info("최소 목표퍼센트 이상 달성 실패");
            fundingItem.updateFundingState(FundingState.FAIL);//update안됨
            //펀딩 참여자 가져오기
            List<Long> fundingMemberId = fundingMemberRepository.getFundingItemIdMatchesFundingItem(fundingItem);
            fundingMemberId.forEach(id -> {
                //예약결제 취소
                unschedulePayment(id);
                //TODO response로 펀딩 실패 메일 보내기

                EmailMessage emailMessage = EmailMessage.builder()
                        .to(getReceiverList(fundingItem))
                        .subject("HABDAY" + "펀딩 실패 알림" )
                        .message("'" + fundingItem.getFundingName()+"' 펀딩이 실패했습니다. \n" +
                                "실패한 펀딩은 결제처리가 되지 않습니다.")
                        .build();
                emailService.sendEmail(emailMessage);
            });
            //throw new CustomException(FAIL_FINISH_FUNDING);
        }

        //fundingItemRepository.save(fundingItem);
    }

    // 펀딩 기간 만료 후, 펀딩 목표 퍼센트 달성 했는지 여부 확인 로직
    public void checkFundingFinishDate(Long fundingItemId){
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));

        LocalDate now = LocalDate.now(); //현재 날짜 구하기
        System.out.println("now^^ " + now.isEqual(fundingItem.getFinishDate()));

        if (now.compareTo(fundingItem.getFinishDate()) == 0){
            checkFundingGoalPercent(fundingItem);
        } else if(now.compareTo(fundingItem.getFinishDate()) < 0){
            log.info("종료 이전");
            throw new CustomException(NOT_FINISH_FUNDING); // 펀딩이 아직 종료되지 않음
        }else{
            log.info("종료 이후");
            throw new CustomException(ALREADY_FINISHED_FUNDING);
        }
    }

    public String[] getReceiverList(FundingItem fundingItem){
        List<String> mailList = fundingMemberRepository.getMailList(fundingItem);
        log.info("mailList: "  + new Gson().toJson(mailList));
        return mailList.toArray(new String[mailList.size()]);
    }

    public void unschedulePayment(Long id){
        log.info("unschedulePayment: start");
        NoneAuthPayUnscheduleRequestDto request = new NoneAuthPayUnscheduleRequestDto(id,  "목표 달성 실패로 인한 결제 취소");
        Call<UnscheduleResponseDto> call = restService.unscheduleApi(request);//예약결제 취소 후 fundingMember status cancel로 업데이트
        try {
            Response<UnscheduleResponseDto> response = call.execute();//각각의 요청에 대해서만 익셉션이 생길 테니까 익셉션이 이 함수까지는 안오겠지,,?
            log.info("unschedulePayment response: " + new Gson().toJson(response.body()));
        } catch (IOException e) {
            log.info("unschedule Paymentretrofit 오류: " + e);
            //throw new CustomException(FAIL_WHILE_UNSCHEDULING);
        }
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
        String clientIp = getIp(request);
        String[] ips = {"52.78.100.19", "52.78.48.223", "52.78.5.241"};
        List<String> ipLists = new ArrayList<>(Arrays.asList(ips));

        FundingMember fundingMember = fundingMemberRepository.findByMerchantId(callbackRequestDto.getMerchant_uid());
        FundingItem fundingItem = fundingMember.getFundingItem();
        BigDecimal amount = fundingMember.getAmount();

        IamportResponse<Payment> response = iamportService.paymentByImpUid(callbackRequestDto.getImp_uid());
        log.info("response: " + new Gson().toJson(response));

        if (!ipLists.contains(clientIp)){
            fundingMember.updateWebhookFail(fail, "ip주소가 맞지 않음");
            log.info("callbackSchedule() ip 주소 안맞음");
            return;
            //throw new CustomException(UNAUTHORIZED_IP);
            //exception 날리면 트랜잭션이 롤백되어버려 영속성컨텍스트 flush 안됨
        }

        log.info("callbackSchedule(): member-amount : " + amount);
        log.info("callbackSchedule() 결제 금액 안맞음 " + response.getResponse().getMerchantUid());
        log.info("callbackSchedule() 결제 금액 안맞음 isEqual" + amount.equals(response.getResponse().getAmount()));
        log.info("callbackSchedule() 결제 금액 안맞음 compareTo" + amount.compareTo(response.getResponse().getAmount()));

//        if(!amount.equals(response.getResponse().getAmount())){
//            fundingMember.updateWebhookFail(fail, "결제 금액이 맞지 않음");
//            log.info("callbackSchedule(): member-amount : " + amount);
//            log.info("callbackSchedule() 결제 금액 안맞음 " + response.getResponse().getMerchantUid());
//            return;
//            //throw new CustomException(NO_CORRESPONDING_AMOUNT);
//        }
        String[] receiver = {fundingMember.getMember().getEmail()};

        if(callbackRequestDto.getStatus() == paid.getMsg()){
            fundingMember.updateWebhookSuccess(paid);
            log.info("callbackSchedule() paid로 update" + response.getResponse().getMerchantUid());
            // TODO 결제 성공 메일 보내기

            EmailMessage emailMessage = EmailMessage.builder()
                    .to(receiver)
                    .subject("HABDAY" + "결제 성공 알림" )
                    .message("'" + fundingItem.getFundingName()+"' 결제가 성공했습니다. \n" +
                            "총 결제 금액은 : " + amount + "입니다.\n" +
                            "참여한 펀딩 보기: " + CmnConst.server + "funding/showFundingContent?itemId=" + fundingItem.getId())
                    .build();
            emailService.sendEmail(emailMessage);
        }else{
            fundingMember.updateWebhookFail(fail, response.getResponse().getFailReason());
            log.info("callbackSchedule() fail로 update" + response.getResponse().getMerchantUid());
            // TODO 수동 결제 링크 보내기
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(receiver)
                    .subject("HABDAY" + "결제 실패 알림" )
                    .message("'" + fundingItem.getFundingName()+"' 결제가 실패했습니다. \n" +
                            "결제 실패 이유는 " + "입니다. \n" +
                            "다시 결제: " + " \n" +
                            "참여한 펀딩 보기: " + CmnConst.server + "funding/showFundingContent?itemId=" + fundingItem.getId())
                    .build();
            emailService.sendEmail(emailMessage);
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

}