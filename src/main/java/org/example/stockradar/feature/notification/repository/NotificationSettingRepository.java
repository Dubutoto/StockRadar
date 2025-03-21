package org.example.stockradar.feature.notification.repository;

import org.example.stockradar.feature.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * @author Hyun7en
 */

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    // 회원별 모든 알림 설정 조회
    List<NotificationSetting> findByMember_MemberId(String memberId);

    // 회원별 활성화된 알림 설정 조회 (예: 이메일, SMS, Discord 등)
    List<NotificationSetting> findByMember_MemberIdAndEnabledTrue(String memberId);

}
