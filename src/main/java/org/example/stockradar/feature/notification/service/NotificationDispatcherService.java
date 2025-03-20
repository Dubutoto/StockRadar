package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.auth.service.CoolsmsService;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final CoolsmsService coolSMSService;
    private final EmailService emailService;
    private final DiscordService discordService;
    private final WebPushService webPushService;

    public void dispatchNotification(NotificationEvent event) {
        List<NotificationChannel> channels = event.getChannels();

        // 채널 설정이 없으면 기본 채널(이메일)로 발송
        if (channels == null || channels.isEmpty()) {
            try {
                emailService.sendEmailNotification(
                        event.getEmailAddress(),
                        event.getNotificationType(),
                        event.getProductName(),
                        event.getProductUrl(),
                        event.getStockStatus());
            } catch (Exception e) {
                log.error("기본 채널(이메일) 전송 실패: {}", e.getMessage());
            }
            return;
        }

        // 각 채널별 알림 전송
        for (NotificationChannel channel : channels) {
            try {
                switch (channel) {
                    case SMS:
                        coolSMSService.sendSmsNotification(
                                event.getNotificationType(),
                                event.getPhoneNumber(),
                                event.getProductName(),
                                event.getProductUrl(),
                                event.getStockStatus());
                        break;
                    case EMAIL:
                        emailService.sendEmailNotification(
                                event.getEmailAddress(),
                                event.getNotificationType(),
                                event.getProductName(),
                                event.getProductUrl(),
                                event.getStockStatus());
                        break;
                    case DISCORD:
                        // unified Discord 전송: 내부에서 알림 유형에 따른 분기를 처리
                        discordService.sendDirectMessageNotification(
                                event.getNotificationType(),
                                event.getDiscordUserId(),
                                event.getProductName(),
                                event.getProductUrl(),
                                event.getStockStatus());
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
