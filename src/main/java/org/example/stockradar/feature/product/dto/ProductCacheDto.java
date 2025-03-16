package org.example.stockradar.feature.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCacheDto {
    private Long productId;
    private String productName;
    private String productUrl;
    private Long price;
    private Integer availability;
    private LocalDateTime lastUpdated;
}
