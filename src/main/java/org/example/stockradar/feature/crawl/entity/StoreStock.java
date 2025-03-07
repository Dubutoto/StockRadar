package org.example.stockradar.feature.crawl.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "store_stock",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "store_name", "url"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 스토어명 컬럼: store_name
    @Column(name = "store_name", nullable = false)
    private String storeName;

    // 크롤링할 상품 URL
    @Column(nullable = false)
    private String url;

    // 재고 여부 컬럼: in_stock
    @Column(name = "in_stock", nullable = false)
    private boolean inStock;

    // 가격
    private int price;

    // DB에서 자동으로 현재시간이 들어가고, UPDATE될 때마다 갱신
    @Column(name = "updated_at",
            nullable = false,
            insertable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}

