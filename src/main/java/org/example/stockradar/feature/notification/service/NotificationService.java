package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.example.stockradar.feature.notification.dto.NotificationSettingsDto;
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.example.stockradar.feature.notification.entity.NotificationSetting;
import org.example.stockradar.feature.notification.repository.MemberNotificationRepository;
import org.example.stockradar.feature.notification.repository.NotificationSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

//알림 관련 로직 service
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // 메모리 내에서 현재 설정을 관리 (실제 환경에서는 캐시나 DB를 사용할 수 있음)
    private final NotificationSettingRepository notificationSettingRepository;
    private final MemberNotificationRepository memberNotificationRepository;
    private final MemberRepository memberRepository;
    private final KafkaNotificationProducer kafkaNotificationProducer;

    /**
     * 관심 상품 알림 삭제
     * @param interestProductId
     * @param memberId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotificationsByInterestProductId(Long interestProductId, String memberId) {
        long deletedCount = memberNotificationRepository.deleteByInterestProductIdAndMember_MemberId(interestProductId, memberId);
        log.info("Deleted {} notifications for InterestProductId: {} and MemberId: {}", deletedCount, interestProductId, memberId);
        if (deletedCount == 0) {
            log.warn("No notifications found for deletion for InterestProductId: {} and MemberId: {}", interestProductId, memberId);
        }
    }

    /**
     * 사용자 알림 채널 설정 업데이트
     * 회원과 관심 상품에 해당하는 기존 설정을 삭제하고, 새롭게 DTO 값에 따라 설정을 저장합니다.
     */
    @Transactional
    public void updateSettings(String memberId, NotificationSettingsDto settingsDto) {
        // 기존 설정 삭제 (회원별로 저장된 설정 모두 삭제)
        List<NotificationSetting> existingSettings = notificationSettingRepository.findByMember_MemberId(memberId);
        if (existingSettings != null && !existingSettings.isEmpty()) {
            notificationSettingRepository.deleteAll(existingSettings);
        }

        Member member = memberRepository.findByMemberId(memberId);


        // 새 설정 생성 (관심 상품 연관은 사용하지 않으므로 null 처리)
        List<NotificationSetting> newSettings = new ArrayList<>();
        // 이메일 설정
        newSettings.add(NotificationSetting.builder()
                .member(member) // 이미 조회한 member 객체 사용
                .channel(NotificationChannel.EMAIL)
                .enabled(settingsDto.isEmailNotification())
                .build());
        // SMS 설정
        newSettings.add(NotificationSetting.builder()
                .member(member)
                .channel(NotificationChannel.SMS)
                .enabled(settingsDto.isSmsNotification())
                .build());
        // Discord 설정
        newSettings.add(NotificationSetting.builder()
                .member(member)
                .channel(NotificationChannel.DISCORD)
                .enabled(settingsDto.isDiscordNotification())
                .build());

        notificationSettingRepository.saveAll(newSettings);
        log.info("사용자 {}의 알림 설정 업데이트 완료: {}", memberId, settingsDto);
    }

    /**
     * DB에 저장된 사용자 알림 설정을 기반으로, 해당 회원에 대해 활성화된 채널을 조회하고,
     * 알림 이벤트를 생성하여 Kafka를 통해 전송합니다.
     *
     * @param memberId         사용자 ID (예: 이메일)
     * @param interestProductId 관심 상품 ID
     * @param messageContent   알림 메시지 내용
     */
    @Transactional(readOnly = true)
    public void sendNotificationEvent(String memberId, Long interestProductId, String messageContent) {
        // 회원별 활성화된 설정 조회 (관심 상품 연관 없이)
        List<NotificationSetting> activeSettings = notificationSettingRepository
                .findByMember_MemberIdAndEnabledTrue(memberId);

        List<NotificationChannel> channels = new ArrayList<>();
        for (NotificationSetting setting : activeSettings) {
            channels.add(setting.getChannel());
        }

        // 실제 사용자 이메일, SMS 번호, Discord Webhook URL은 구독 정보 등에서 가져와야 합니다.
        // 여기서는 간단히 memberId를 이메일 주소로 사용합니다.
        NotificationEvent event = NotificationEvent.builder()
                .interestProductId(interestProductId)
                .emailAddress(memberId)
                .messageContent(messageContent)
                .channels(channels)
                .build();

        kafkaNotificationProducer.sendNotification(event);
        log.info("알림 이벤트 전송: {}", event);
    }


}
