package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String topic = "notification-events"; // Kafka 토픽 이름

    public void sendNotification(NotificationEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
