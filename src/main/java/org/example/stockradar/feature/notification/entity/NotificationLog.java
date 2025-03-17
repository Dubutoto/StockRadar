package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Hyun7en
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 회원 알림(MemberNotification)과 연관되어 있는지
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_notification_id")
    private MemberNotification memberNotification;

    // 전송 상태 (PENDING, SENT, FAILED)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    // 전송 시각
    private LocalDateTime sentAt;

    // 전송 실패 시 에러 메시지
    private String errorMessage;
}

