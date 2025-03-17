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
public class ProductResponseDto {
    private String productName;
    private Integer availability;
    private Long price;
    private LocalDateTime lastUpdated;
    private String redirectUrl;
    private int status;
    private String message;
    private List data;
    private Long productId;
    private String productUrl;

    // 쿼리에서 필요한 생성자 추가
    public ProductResponseDto(Long productId, String productName,
                              Integer availability, Long price,
                              LocalDateTime lastUpdated, String productUrl) {
        this.productId = productId;
        this.productName = productName;
        this.availability = availability;
        this.price = price;
        this.lastUpdated = lastUpdated;
        this.productUrl = productUrl;
    }

}
