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
    private String discordUserId;               // 디스코드 DM 전송용 사용자 ID
    private String messageContent;              // 기본 메시지 내용
    // 추가 정보
    private String productName;                 // 상품 이름
    private String productUrl;                  // 상품 URL
    private String stockStatus;                 // 재고 상태 (재고 변경 알림 시 사용)
    private String notificationType;            // 알림 유형 ("registration", "stockChange" 등)
}
