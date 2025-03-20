package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Hyun7en
 */

//알림 저장
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 제목 (예: “상품 재고 입고” 등)
    private String title;

    // 알림 내용
    private String content;

    // 추가 정보 (예: 상품 링크, 이미지 등)
    private String extraData;

    // 생성 시각 (audit 용)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

