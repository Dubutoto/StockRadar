package org.example.stockradar.feature.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hyun7en
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationSettingsDto {
    private boolean emailNotification;
    private boolean smsNotification;
    private boolean discordNotification;
}

//세팅 설정 NotificationEvent의 List<NotificationChannel> channels에 전달