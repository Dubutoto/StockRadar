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
public class InterestProductRequestDto {

    private Long productId;

}
