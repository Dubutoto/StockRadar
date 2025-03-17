package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class DiscordService {

    private final JDA jda;

    /**
     * 지정된 사용자 ID로 직접 DM을 전송합니다.
     * @param userId 디스코드 사용자 ID
     * @param messageContent 전송할 메시지 내용
     */
    public void sendDirectMessage(String userId, String messageContent) {
        jda.retrieveUserById(userId).queue(user ->
                user.openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage(messageContent).queue()
                )
        );
    }
}
