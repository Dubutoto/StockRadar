package org.example.stockradar.feature.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStaticCacheDto {
    private String productName;
    private Long price;
    private Long productId;
    private String productUrl;

}
