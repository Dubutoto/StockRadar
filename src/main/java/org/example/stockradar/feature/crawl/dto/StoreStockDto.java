package org.example.stockradar.feature.crawl.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * 스토어별 재고, 가격 등의 정보를 컨트롤러에서 반환할 때 사용
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreStockDto {
    private String storeName;    // 예: "AMAZON"
    private boolean inStock;     // 재고
    private BigDecimal price;    // 가격
}

