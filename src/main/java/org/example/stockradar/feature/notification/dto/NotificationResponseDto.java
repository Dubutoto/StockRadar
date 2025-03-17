package org.example.stockradar.feature.notification.dto;

import lombok.*;

/**
 * @author Hyun7en
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationResponseDto {
    private String alert;
}
