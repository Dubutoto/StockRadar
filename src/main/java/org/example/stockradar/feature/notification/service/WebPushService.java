package org.example.stockradar.feature.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
@Slf4j
public class WebPushService {

    public void sendWebPush(Long interestProductId, String messageContent) {
        log.info("Sending Web Push for InterestProduct ID: {} with message: {}", interestProductId, messageContent);
        // 실제 구현 시, FCM 등 외부 서비스 연동 및 예외 처리 로직 추가
        // 예: try { fcmClient.sendNotification(interestProductId, messageContent); } catch(Exception e) { log.error("Web Push 전송 실패: {}", e.getMessage()); }
    }
}
