package org.example.stockradar.feature.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // 이메일 템플릿 리소스를 application.properties 또는 application.yml에서 주입받음
    @Value("classpath:templates/email-template/email-template1.html")
    private Resource emailTemplate;

    /**
     * HTML 템플릿 파일을 로드하고, 제품 정보로 플레이스홀더를 치환합니다.
     *
     * @param productName 제품 이름
     * @param productUrl  제품 URL
     * @return 치환 완료된 HTML 콘텐츠
     */
    public String loadHtmlTemplate(String productName, String productUrl) {
        try {
            String template = StreamUtils.copyToString(emailTemplate.getInputStream(), StandardCharsets.UTF_8);
            template = template.replace("{{productName}}", productName);
            template = template.replace("{{interestProductUrl}}", productUrl);
            return template;
        } catch (IOException e) {
            log.error("HTML 템플릿 로드 실패", e);
            return "";
        }
    }

    /**
     * HTML 포맷의 이메일을 전송합니다.
     *
     * @param to          수신자 이메일 주소
     * @param subject     이메일 제목
     * @param htmlContent 이메일 본문 (HTML)
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML 이메일 전송 성공: {}", to);
        } catch (Exception e) {
            log.error("HTML 이메일 전송 실패: {}", e.getMessage());
        }
    }
}
