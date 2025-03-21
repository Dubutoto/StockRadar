package org.example.stockradar.feature.notification.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class EmailQueueService {

    private final EmailService emailService;

    // 이메일 전송 요청을 저장할 BlockingQueue
    private final BlockingQueue<NotificationEvent> emailQueue = new LinkedBlockingQueue<>();

    // 동시에 실행할 워커 스레드 수 (환경에 맞게 조정 가능)
    private final int WORKER_COUNT = 5;

    @PostConstruct
    public void startEmailWorkers() {
        for (int i = 0; i < WORKER_COUNT; i++) {
            Thread worker = new Thread(() -> {
                while (true) {
                    try {
                        // 큐에서 이벤트를 꺼내 이메일 전송 실행
                        NotificationEvent event = emailQueue.take();
                        emailService.sendEmailNotification(
                                event.getEmailAddress(),
                                event.getNotificationType(),
                                event.getProductName(),
                                event.getProductUrl(),
                                event.getStockStatus()
                        );
                        log.info("이메일 전송 완료: {}", event);
                    } catch (Exception e) {
                        log.error("이메일 전송 실패: {}", e.getMessage());
                        // 실패 시 재시도 로직 추가 가능
                    }
                }
            });
            worker.setDaemon(true);
            worker.setName("Email-Worker-" + i);
            worker.start();
        }
    }

    // 외부에서 EMAIL 전송 이벤트를 큐에 추가하는 메서드
    public void enqueue(NotificationEvent event) {
        emailQueue.offer(event);
        log.info("이메일 전송 큐에 이벤트 추가: {}", event);
    }
}
