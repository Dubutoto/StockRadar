package org.example.stockradar.feature.crawl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.crawl.entity.Price;
import org.example.stockradar.feature.crawl.entity.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class StockStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long stockId;

    @Column(nullable = false)
    private Integer availability;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;


    @OneToOne(mappedBy = "stockStatus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Price price;



    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
