package com.habday.server.config.email;

import com.google.gson.Gson;
import com.habday.server.classes.Common;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.List;

import static com.habday.server.constants.code.ExceptionCode.FAIL_SENDING_MAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService extends Common {
    private final JavaMailSender javaMailSender;

    public Boolean sendEmail(EmailMessage emailMessage){
        if (emailMessage.getTo() == null){
            log.info("이메일을 보낼 참여자가 없음");
            return false;
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            mimeMessageHelper.setText(emailMessage.getMessage(), false); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);
            log.info("mail sending Success");
            return true;
        }catch (MessagingException e){
            log.info("mail sending fail" + e);
            return false;
            //throw new CustomException(FAIL_SENDING_MAIL);
        }
    }

    public String[] getParticipantEmail(FundingItem fundingItem){
        List<String> mailList = fundingMemberRepository.getMailList(fundingItem);
        log.info("mailList: "  + new Gson().toJson(mailList));
        return mailList.toArray(new String[mailList.size()]);
    }
}
