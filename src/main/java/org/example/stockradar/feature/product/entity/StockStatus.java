package org.example.stockradar.feature.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "StockStatus")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer stockId;


    @Column( nullable = false)
    private String availability;

    @Column( nullable = false)
    private Integer price;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",nullable = false )
    private LocalDateTime lastUpdated;



    //판매처 관계설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId")
    private Store store;

    //일대일 관계
    //상품과 관계설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;


    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
    }
}
