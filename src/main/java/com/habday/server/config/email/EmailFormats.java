package com.habday.server.config.email;

import com.habday.server.constants.CmnConst;
import com.habday.server.domain.fundingItem.FundingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.habday.server.constants.CmnConst.webAddress;

@RequiredArgsConstructor
@Component
public class EmailFormats {
    public final EmailService emailService;

    public void sendFundingConfirmEmail(FundingItem fundingItem){
        EmailMessage email = EmailMessage.builder()
                .to(emailService.getParticipantEmail(fundingItem))
                .subject("HABDAY" + "펀딩 인증 알림" )
                .message("'" + fundingItem.getFundingName()+"'에 대한 선물하신 금액의 사용처가 생일자에 의해 인증되었습니다.  \n" +
                        "펀딩 인증은 " + webAddress + fundingItem.getId() + "에서 볼 수 있습니다.")
                .build();
        emailService.sendEmail(email);

    }

    public void sendFundingSuccessEmail(FundingItem fundingItem){
        EmailMessage email =  EmailMessage.builder()
                .to(emailService.getParticipantEmail(fundingItem))
                .subject("HABDAY" + "펀딩 성공 알림" )
                .message("'" + fundingItem.getFundingName()+"' 펀딩이 성공했습니다. \n" +
                        "00시 " + CmnConst.paymentDelayMin + "분에 결제 처리될 예정입니다.")
                .build();
        emailService.sendEmail(email);
    }

    public void sendFundingFailEmail(FundingItem fundingItem){
        EmailMessage email = EmailMessage.builder()
                .to(emailService.getParticipantEmail(fundingItem))
                .subject("HABDAY" + "펀딩 실패 알림" )
                .message("'" + fundingItem.getFundingName()+"' 펀딩이 실패했습니다. \n" +
                        "실패한 펀딩은 결제처리가 되지 않습니다.")
                .build();
        emailService.sendEmail(email);
    }

    public void sendPaymentSuccessEmail(FundingItem fundingItem, String[] receiver, BigDecimal amount){
        EmailMessage email = EmailMessage.builder()
                .to(receiver)
                .subject("HABDAY" + "결제 성공 알림" )
                .message("'" + fundingItem.getFundingName()+"' 결제가 성공했습니다. \n" +
                        "총 결제 금액은 : " + amount + "입니다.\n" +
                        "참여한 펀딩 보기: " + webAddress + fundingItem.getId())
                .build();
        emailService.sendEmail(email);
    }

    public void sendPaymentFailEmail(FundingItem fundingItem, String[] receiver){
        EmailMessage email = EmailMessage.builder()
                .to(receiver)
                .subject("HABDAY" + "결제 실패 알림" )
                .message("'" + fundingItem.getFundingName()+"' 결제가 실패했습니다. \n" +
                        "결제 실패 이유는 " + "입니다. \n" +
                        "다시 결제: " + " \n" +
                        "참여한 펀딩 보기: " + webAddress + fundingItem.getId())
                .build();
        emailService.sendEmail(email);
    }

    public void sendFundingCanceledEmail(FundingItem fundingItem){
        EmailMessage email = EmailMessage.builder()
                .to(emailService.getParticipantEmail(fundingItem))
                .subject("HABDAY" + "펀딩 삭제 알림" )
                .message("참여하신 '" + fundingItem.getFundingName()+"' 펀딩이 삭제되었습니다.. \n" +
                        "삭제된 펀딩은 결제처리가 되지 않습니다.")
                .build();
        emailService.sendEmail(email);
    }
}
