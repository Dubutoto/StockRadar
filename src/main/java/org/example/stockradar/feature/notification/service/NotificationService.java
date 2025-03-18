package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.notification.dto.NotificationSettingsDto;
import org.example.stockradar.feature.notification.repository.MemberNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//알림 관련 로직 service
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // 메모리 내에서 현재 설정을 관리 (실제 환경에서는 캐시나 DB를 사용할 수 있음)
    private NotificationSettingsDto currentSettings = new NotificationSettingsDto(true, false, false);
    private final MemberNotificationRepository memberNotificationRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deleteNotificationsByInterestProductId(Long interestProductId, String memberId) {
        long deletedCount = memberNotificationRepository.deleteByInterestProductIdAndMember_MemberId(interestProductId, memberId);
        log.info("Deleted {} notifications for InterestProductId: {} and MemberId: {}", deletedCount, interestProductId, memberId);
        if (deletedCount == 0) {
            log.warn("No notifications found for deletion for InterestProductId: {} and MemberId: {}", interestProductId, memberId);
        }
    }

    /**
     * 사용자 알림 채널 설정 업데이트 (DB 등 영구 저장 없이 메모리에 저장)
     * @param settingsDto 클라이언트로부터 전달된 설정 DTO
     */
    public void updateSettings(NotificationSettingsDto settingsDto) {
        // 단순히 메모리 내 설정을 갱신합니다.
        currentSettings = settingsDto;
        log.info("알림 설정 업데이트됨: {}", currentSettings);
    }

}
