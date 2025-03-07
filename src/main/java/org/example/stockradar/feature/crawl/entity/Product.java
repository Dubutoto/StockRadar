package org.example.stockradar.feature.crawl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long productId;


    @Column(length = 100, nullable = false)
    private String productName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String productUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;

    @OneToOne(mappedBy = "product",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private StockStatus stockStatus;

}
