package org.example.stockradar.feature.notification.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.auth.service.CoolsmsService;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.springframework.stereotype.Service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsQueueService {

    private final CoolsmsService coolsmsService;

    // SMS 전송 요청을 저장할 BlockingQueue
    private final BlockingQueue<NotificationEvent> smsQueue = new LinkedBlockingQueue<>();

    // 동시에 실행할 워커 스레드 수 (필요에 따라 조정)
    private final int WORKER_COUNT = 5;

    @PostConstruct
    public void startSmsWorkers() {
        for (int i = 0; i < WORKER_COUNT; i++) {
            Thread worker = new Thread(() -> {
                while (true) {
                    try {
                        NotificationEvent event = smsQueue.take();
                        coolsmsService.sendSmsNotification(
                                event.getNotificationType(),
                                event.getPhoneNumber(),
                                event.getProductName(),
                                event.getProductUrl(),
                                event.getStockStatus()
                        );
                        log.info("SMS 전송 완료: {}", event);
                    } catch (Exception e) {
                        log.error("SMS 전송 실패: {}", e.getMessage());
                        // 필요 시 재시도 로직 추가 가능
                    }
                }
            });
            worker.setDaemon(true);
            worker.setName("SMS-Worker-" + i);
            worker.start();
        }
    }

    // 외부에서 SMS 전송 이벤트를 큐에 추가하는 메서드
    public void enqueue(NotificationEvent event) {
        smsQueue.offer(event);
        log.info("SMS 전송 큐에 이벤트 추가: {}", event);
    }
}
