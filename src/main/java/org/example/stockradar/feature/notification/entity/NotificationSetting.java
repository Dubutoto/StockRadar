package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;

/**
 * @author Hyun7en
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자와의 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code")
    private Member member;

    // 알림 유형 (예: 메시지, 경고 등)
    private String notificationType;

    // 해당 알림 유형 활성화 여부
    private boolean enabled;

}

