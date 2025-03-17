package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.auth.entity.Member;
import java.time.LocalDateTime;

/**
 * @author Hyun7en
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "member_notifications")
public class MemberNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림을 받을 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 전송된 알림
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    // 알림이 발생한 관심 상품과의 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_product_id", nullable = false)
    private InterestProduct interestProduct;

    // 알림 읽음 여부
    private boolean isRead;

    // 읽은 시각 (읽지 않았다면 null)
    private LocalDateTime readAt;

    // 생성 시각 (알림 생성 시각)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

