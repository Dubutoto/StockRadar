package org.example.stockradar.feature.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine; // Thymeleaf Template Engine

    /**
     * Thymeleaf 템플릿 엔진을 사용하여 이메일 콘텐츠를 생성합니다.
     *
     * @param notificationType 알림 유형 ("stockChange" 또는 "registration" 등)
     * @param productName      제품 이름
     * @param interestProductUrl 등록된 상품 URL
     * @param stockStatus      재고 상태 (재고 변경 알림인 경우 사용, 등록 알림일 경우 빈 문자열)
     * @return 최종 이메일 본문 (HTML)
     */
    public String generateEmailContent(String notificationType, String productName, String interestProductUrl, String stockStatus) {
        Context context = new Context();
        // 템플릿 내에서 조건부 처리를 위해 notificationType 변수를 사용합니다.
        context.setVariable("notificationType", notificationType);
        context.setVariable("productName", productName);
        context.setVariable("interestProductUrl", interestProductUrl);
        context.setVariable("stockStatus", stockStatus);
        // 템플릿 파일 이름은 resources/templates/email-template.html 을 기준으로 합니다.
        return templateEngine.process("email-template/email-template", context);
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
