package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

//디스코드 채널로 발송
@Service
@Slf4j
@RequiredArgsConstructor
public class DiscordService {

    private final JDA jda;

    /**
     * 지정된 사용자 ID로 직접 DM을 전송합니다.
     * @param userId 디스코드 사용자 ID
     * @param messageContent 전송할 메시지 내용
     */
    public void sendDirectMessage(String userId, String messageContent) {
        jda.retrieveUserById(userId).queue(
                user -> user.openPrivateChannel().queue(
                        privateChannel -> privateChannel.sendMessage(messageContent).queue(
                                success -> log.info("Discord DM 전송 성공: {}", userId),
                                error -> log.error("Discord DM 전송 실패: {} - {}", userId, error.getMessage())
                        ),
                        error -> log.error("Discord 개인 채널 열기 실패: {} - {}", userId, error.getMessage())
                ),
                error -> log.error("Discord 사용자 조회 실패: {} - {}", userId, error.getMessage())
        );
    }
}
