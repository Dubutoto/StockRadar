package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.CoolsmsService;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.notification.dto.InterestProductRequestDto;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.example.stockradar.feature.notification.entity.NotificationSetting;
import org.example.stockradar.feature.notification.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hyun7en
 */

//사용자 알림 채널 설정에 따른 알림 발송 처리
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final CoolsmsService coolSMSService;
    private final EmailService emailService;
    private final DiscordService discordService;
    private final WebPushService webPushService;
    private final InterestProductService interestProductService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final KafkaNotificationProducer kafkaNotificationProducer;
    private final NotificationSettingRepository notificationSettingRepository;

    @Value("classpath:templates/email-template/email-template1.html")
    private Resource emailTemplate;

    private String loadHtmlTemplate(String productName, String productUrl) {
        try {
            String template = StreamUtils.copyToString(emailTemplate.getInputStream(), StandardCharsets.UTF_8);
            // 템플릿의 플레이스홀더를 실제 값으로 치환
            template = template.replace("{{productName}}", productName);
            template = template.replace("{{interestProductUrl}}", productUrl);
            return template;
        } catch (IOException e) {
            log.error("HTML 템플릿 로드 실패", e);
            return "";
        }
    }

    public void dispatchNotification(NotificationEvent event) {
        List<NotificationChannel> channels = event.getChannels();
        if (channels == null || channels.isEmpty()) {
            try {
                emailService.sendHtmlEmail(event.getEmailAddress(), "알림", event.getMessageContent());
            } catch (Exception e) {
                log.error("기본 채널(이메일) 전송 실패: {}", e.getMessage());
            }
            return;
        }
        for (NotificationChannel channel : channels) {
            try {
                switch (channel) {
                    case SMS:
                        coolSMSService.sendSms(event.getPhoneNumber(), event.getMessageContent());
                        break;
                    case EMAIL:
                        emailService.sendHtmlEmail(event.getEmailAddress(), "알림", event.getMessageContent());
                        break;
                    case DISCORD:
                        discordService.sendDirectMessage(event.getDiscordWebhookUrl(), event.getMessageContent());
                        break;
                    case WEB_PUSH:
                        webPushService.sendWebPush(event.getInterestProductId(), event.getMessageContent());
                        break;
                    default:
                        log.warn("알 수 없는 알림 채널: {}", channel);
                        break;
                }
            } catch (Exception e) {
                log.error("{} 채널 전송 실패: {}", channel, e.getMessage());
            }
        }
    }
}
