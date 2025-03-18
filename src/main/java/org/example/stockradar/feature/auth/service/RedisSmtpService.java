package org.example.stockradar.feature.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisSmtpService {
    private final RedisTemplate<String, String> redisTemplate;
    private final SmtpMailService smtpMailService;

    // 이메일 알림 관련 설정
    private static final String ADMIN_EMAIL = "krt8599@naver.com";
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1초

    // 알림 상태 관리 (중복 알림 방지)
    private boolean redisAlertSent = false;
    private LocalDateTime lastAlertTime = null;
    private static final Duration ALERT_COOLDOWN = Duration.ofHours(1); // 알림 재발송 간격

    /**
     * Redis 서버 연결 상태 확인
     * 최대 3번 시도 후 실패 시 이메일 알림 발송
     */
    public boolean checkRedisConnection() {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                // Redis 연결 상태 확인
                RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
                connectionFactory.getConnection().ping();

                return true;
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                log.warn("Redis 서버 연결 실패 (시도 {}/{}): {}", retryCount, MAX_RETRY_COUNT, e.getMessage());

                // 재시도 전 잠시 대기
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // 최대 시도 횟수 초과 - 이메일 알림 발송
        if (!redisAlertSent || canSendAlertAgain()) {
            sendRedisDownAlert(lastException);
        }

        return false;
    }

    /**
     * Redis 서버 다운 알림 이메일 발송
     */
    private void sendRedisDownAlert(Exception exception) {
        String subject = "[긴급] Redis 서버 다운 알림";
        String body = String.format(
                "Redis 서버 연결이 %d번 실패하여 서버가 다운된 것으로 판단됩니다.\n\n" +
                        "시간: %s\n" +
                        "오류 메시지: %s\n\n" +
                        "가능한 빨리 Redis 서버 상태를 확인해주세요.",
                MAX_RETRY_COUNT,
                LocalDateTime.now(),
                exception != null ? exception.getMessage() : "알 수 없는 오류"
        );

        try {
            smtpMailService.sendMail(ADMIN_EMAIL, subject, body);
            redisAlertSent = true;
            lastAlertTime = LocalDateTime.now();
            log.info("Redis 서버 다운 알림 이메일 발송 완료: {}", ADMIN_EMAIL);
        } catch (Exception e) {
            log.error("Redis 서버 다운 알림 이메일 발송 실패: {}", e.getMessage(), e);
        }
    }


    /**
     * 알림 재발송 가능 여부 확인 (쿨다운 시간 체크)
     */
    private boolean canSendAlertAgain() {
        if (lastAlertTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(lastAlertTime.plus(ALERT_COOLDOWN));
    }

}

