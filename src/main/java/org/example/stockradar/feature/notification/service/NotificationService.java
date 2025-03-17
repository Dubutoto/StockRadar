package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.notification.repository.MemberNotificationRepository;
import org.example.stockradar.feature.notification.repository.NotificationRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MemberNotificationRepository memberNotificationRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deleteNotificationsByInterestProductId(Long interestProductId, String memberId) {
        memberNotificationRepository.deleteByInterestProductIdAndMember_MemberId(interestProductId, memberId);
    }

}
