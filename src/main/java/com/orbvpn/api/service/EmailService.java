package com.orbvpn.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendMail(String fromEmail, String toEmail, String subject, String body) {
        try {
            log.debug("Sending Email... ", toEmail);

            body = "<img src=\"cid:logo\"><br><br>" + body;
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,true, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.addInline("logo", new ClassPathResource("/image/logo.png"));
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            mailSender.send(message);
            log.debug("Email sent successfully ", toEmail);
        } catch (MessagingException ex) {
            log.error("Failed to send email to {}", toEmail, ex);
        }
    }
}
