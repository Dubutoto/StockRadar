package org.example.stockradar.feature.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer productId;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false, length = 100)
    private String productModel;

    @Column(nullable = false, length = 100)
    private String productManufacturer;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String productUrl;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    //카테고리 관계설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="categoryId")
    private Categories category;

    //팜내처 관계설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="storeId")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY ,mappedBy = "product")
    private StockStatus stockStatus;





}
