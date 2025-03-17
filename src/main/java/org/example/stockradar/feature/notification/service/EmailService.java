package org.example.stockradar.feature.notification.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            // 두 번째 인자 true: HTML 포맷으로 전송
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML 이메일 전송 성공: {}", to);
        } catch (Exception e) {
            log.error("HTML 이메일 전송 실패: {}", e.getMessage());
        }
    }
}
