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

}
