package org.example.stockradar.feature.notification.service;

import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String topic = "notification-events"; // Kafka 토픽 이름

    public KafkaNotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(NotificationEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
