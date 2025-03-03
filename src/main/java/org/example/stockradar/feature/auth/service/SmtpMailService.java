package org.example.stockradar.feature.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpMailService {

    private final JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text) {
        try {
            // MimeMessage 생성
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // MimeMessageHelper: 두 번째 인자(false)는 "멀티파트(HTML)" 여부
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);  // HTML 메일이면 true

            // 메일 전송
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();  // 또는 logger 사용
        }
    }
}