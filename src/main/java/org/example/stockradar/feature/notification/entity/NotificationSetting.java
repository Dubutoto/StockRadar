package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.auth.entity.Member;

/**
 * @author Hyun7en
 */

//회원별 알림 설정 다르게
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 설정을 가진 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberCode")
    private Member member;

    // 알림 채널 (EMAIL, DISCORD, SMS)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    // 해당 채널 알림 활성화 여부
    private boolean enabled;
}

