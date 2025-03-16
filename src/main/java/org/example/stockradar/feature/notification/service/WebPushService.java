package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class WebPushService {

    public void sendWebPush(Long interestProductId, String messageContent) {
        // 실제 웹 푸시 알림 구현은 FCM 등 외부 서비스를 연동하는 방식으로 구현해야 합니다.
        // 아래는 단순 로그 출력 예시입니다.
        System.out.println("Sending Web Push for InterestProduct ID: " + interestProductId + " with message: " + messageContent);
    }
}
