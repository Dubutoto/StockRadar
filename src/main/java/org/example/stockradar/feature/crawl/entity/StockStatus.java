package org.example.stockradar.feature.crawl.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id", nullable = false, unique = true)
    private Long stockId;

    @Column(name = "availability", nullable = false)
    private Integer availability;

    // 마지막으로 알림 전송한 재고 상태 (0: 재고 없음, 1: 재고 있음)
    @Column(name = "last_notified_availability")
    private Integer lastNotifiedAvailability;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(mappedBy = "stockStatus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Price price;

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
