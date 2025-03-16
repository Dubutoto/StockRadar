package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class KafkaNotificationConsumer {

    private final NotificationDispatcherService notificationDispatcherService;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void listen(NotificationEvent event) {
        // 수신한 이벤트를 이용해 각 채널로 알림 전송을 수행합니다.
        notificationDispatcherService.dispatchNotification(event);
    }
}

