package org.example.stockradar.feature.crawl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long priceId;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long price;

    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    @OneToOne
    @JoinColumn(name = "stockId", nullable = false)
    private StockStatus stockStatus;

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }


}
