package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.crawl.entity.Product;
import java.time.LocalDateTime;

/**
 * @author Hyun7en
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "interest_products")
public class InterestProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관심 상품 등록 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberCode")
    private Member member;

    // 상품과의 연관관계 (Product 엔터티 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 웹 푸시는 기본적으로 활성화 (재고 입고 시 기본 알림)
    @Builder.Default
    private boolean webPushNotification = true;

    // 생성 시각 (audit 용)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

