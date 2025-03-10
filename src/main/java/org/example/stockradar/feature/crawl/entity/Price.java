package org.example.stockradar.feature.crawl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id", nullable = false, unique = true)
    private Long priceId;

    @Column(name = "price", columnDefinition = "BIGINT DEFAULT 0")
    private Long price;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    @OneToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private StockStatus stockStatus;

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }
}
