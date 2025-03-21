package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final EmailQueueService emailQueueService;
    private final SmsQueueService smsQueueService;
    private final DiscordQueueService discordQueueService;
    private final WebPushService webPushService;


    public void dispatchNotification(NotificationEvent event) {
        List<NotificationChannel> channels = event.getChannels();

        // 채널 설정이 없으면 기본 EMAIL 채널로 처리
        if (channels == null || channels.isEmpty()) {
            emailQueueService.enqueue(event);
            return;
        }

        // 각 채널별 알림 전송 처리 (모두 큐를 통해 비동기 처리)
        channels.parallelStream().forEach(channel -> {
            try {
                switch (channel) {
                    case SMS:
                        smsQueueService.enqueue(event);
                        break;
                    case EMAIL:
                        emailQueueService.enqueue(event);
                        break;
                    case DISCORD:
                        discordQueueService.enqueue(event);
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
        });
    }
}
