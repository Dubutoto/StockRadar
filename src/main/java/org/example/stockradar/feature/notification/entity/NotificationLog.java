package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * @author Hyun7en
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 발송과 관련된 UserNotification과 연관
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_notification_id")
    private MemberNotification memberNotification;

    // 전송 상태 (예: 성공, 실패)
    private String status;

    // 전송 시각
    private LocalDateTime sentAt;

    // 전송 실패 시 에러 메시지
    private String errorMessage;

}

