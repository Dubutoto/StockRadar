package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscordService {

    private final JDA jda;

    /**
     * 관심 상품 등록 완료 DM 알림을 전송합니다.
     *
     * @param userId      디스코드 사용자 ID
     * @param productName 등록된 상품 이름
     * @param productUrl  등록된 상품 URL
     */
    public void sendRegistrationAlert(String userId, String productName, String productUrl) {
        String messageContent = String.format(
                "관심 상품 등록 완료\n상품: %s\n상품 URL: %s",
                productName, productUrl);
        sendDirectMessage(userId, messageContent);
    }

    /**
     * 재고 상태 변경 DM 알림을 전송합니다.
     *
     * @param userId      디스코드 사용자 ID
     * @param productName 변경된 상품 이름
     * @param productUrl  변경된 상품 URL
     * @param stockStatus 현재 재고 상태 (예: '재고 있음', '재고 없음')
     */
    public void sendStockChangeAlert(String userId, String productName, String productUrl, String stockStatus) {
        String messageContent = String.format(
                "재고 상태 변경 알림\n상품: %s\n상품 URL: %s\n현재 재고 상태: %s",
                productName, productUrl, stockStatus);
        sendDirectMessage(userId, messageContent);
    }

    /**
     * 지정된 디스코드 사용자 ID로 DM(Direct Message)을 전송합니다.
     *
     * @param userId         디스코드 사용자 ID
     * @param messageContent 전송할 메시지 내용
     */
    private void sendDirectMessage(String userId, String messageContent) {
        jda.retrieveUserById(userId).queue(
                user -> user.openPrivateChannel().queue(
                        privateChannel -> privateChannel.sendMessage(messageContent).queue(
                                success -> log.info("Discord DM 전송 성공: {}", user.getId()),
                                error -> log.error("Discord DM 전송 실패: {} - {}", user.getId(), error.getMessage())
                        ),
                        error -> log.error("Discord DM 전송 실패 (채널 열기): {} - {}", user.getId(), error.getMessage())
                ),
                error -> log.error("Discord 사용자 조회 실패: {} - {}", userId, error.getMessage())
        );
    }

}
