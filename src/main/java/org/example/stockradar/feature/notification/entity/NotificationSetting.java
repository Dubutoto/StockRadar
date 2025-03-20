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

    /**
     * 회원과 채널을 입력받아 기본 알림 설정 객체를 생성합니다.
     * 이메일 채널인 경우 기본 활성화(true), 그 외 채널은 기본 비활성화(false)로 설정합니다.
     *
     * @param member  알림 설정을 적용할 회원
     * @param channel 알림 채널 (EMAIL, SMS, DISCORD 등)
     * @return 기본 설정이 적용된 NotificationSetting 객체
     */
    public static NotificationSetting createDefaultSetting(Member member, NotificationChannel channel) {
        boolean defaultEnabled = (channel == NotificationChannel.EMAIL);
        return NotificationSetting.builder()
                .member(member)
                .channel(channel)
                .enabled(defaultEnabled)
                .build();
    }
}

