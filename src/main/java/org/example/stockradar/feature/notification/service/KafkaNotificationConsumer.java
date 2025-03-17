package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationConsumer {

    private final NotificationDispatcherService notificationDispatcherService;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void listen(NotificationEvent event) {
        log.info("Kafka에서 이벤트 수신: {}", event);
        try {
            notificationDispatcherService.dispatchNotification(event);
        } catch (Exception e) {
            log.error("Kafka Notification Consumer 처리 실패: {}", e.getMessage());
            // 필요 시 재처리 로직 추가
        }
    }
}
