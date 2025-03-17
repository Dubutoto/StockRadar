package org.example.stockradar.feature.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.stockradar.feature.notification.entity.NotificationChannel;

import java.util.List;

/**
 * @author Hyun7en
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private Long interestProductId;           // 관심 상품 ID
    private List<NotificationChannel> channels; // 선택된 채널 목록
    private String phoneNumber;                 // SMS 전송용 전화번호
    private String emailAddress;                // 이메일 전송용 주소
    private String discordWebhookUrl;           // 디스코드 Webhook URL
    private String messageContent;              // 공통 메시지 내용
}
