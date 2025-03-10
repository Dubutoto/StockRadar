package org.example.stockradar.feature.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Hyun7en
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 제목이나 유형 (ex: 새 메시지, 경고 등) -> 확장 가능성
    private String title;

    // 알림 내용
    private String content;

    // 생성 시각
    private LocalDateTime createdAt;

    // 추가 정보가 필요한 경우 예) 관련 링크 등
    private String extraData;

}

