package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

//kafka 알림 event producer
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String topic = "notification-events"; // Kafka 토픽 이름

    @Retryable(
            value = { Exception.class },
            maxAttempts = 4, // 최초 시도를 포함해 총 4회 시도
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public SendResult<String, NotificationEvent> sendNotificationSync(NotificationEvent event) {
        return kafkaTemplate.send(topic, event).join();
    }

    public void sendNotification(NotificationEvent event) {
        try {
            SendResult<String, NotificationEvent> result = sendNotificationSync(event);
            log.info("Kafka 메시지 전송 성공: {}", result.getRecordMetadata());
        } catch (Exception ex) {
            log.error("Kafka 메시지 전송 실패: {}", ex.getMessage());
            // 필요 시 DLQ 전송 등 추가 처리
        }
    }
}
