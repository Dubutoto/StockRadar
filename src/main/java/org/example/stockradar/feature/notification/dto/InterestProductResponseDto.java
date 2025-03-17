package org.example.stockradar.feature.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hyun7en
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestProductResponseDto {
    private String productId;
    private String productName;
    private String category;
    private String productUrl;
    private int availability;

}
