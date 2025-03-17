package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String topic = "notification-events"; // Kafka 토픽 이름

    public void sendNotification(NotificationEvent event) {
        kafkaTemplate.send(topic, event)
                .thenAccept(result -> log.info("Kafka 메시지 전송 성공: {}", result.getRecordMetadata()))
                .exceptionally(ex -> {
                    log.error("Kafka 메시지 전송 실패: {}", ex.getMessage());
                    // 필요 시 재시도 로직 또는 DLQ 전송 추가
                    return null;
                });
    }

}
