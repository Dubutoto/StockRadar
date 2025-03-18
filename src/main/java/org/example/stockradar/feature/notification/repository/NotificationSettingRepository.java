package org.example.stockradar.feature.notification.repository;

import org.example.stockradar.feature.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    // 회원과 관심 상품에 해당하는 모든 설정 조회
    List<NotificationSetting> findByMember_MemberIdAndInterestProduct_ProductId(String memberId, Long productId);

    // 활성화된 설정만 조회
    List<NotificationSetting> findByMember_MemberIdAndInterestProduct_ProductIdAndEnabledTrue(String memberId, Long productId);
}
