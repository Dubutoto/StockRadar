package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.CoolsmsService;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.notification.dto.InterestProductRequestDto;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.example.stockradar.feature.notification.dto.NotificationSettingsDto;
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.example.stockradar.feature.notification.entity.NotificationSetting;
import org.example.stockradar.feature.notification.repository.MemberNotificationRepository;
import org.example.stockradar.feature.notification.repository.NotificationSettingRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final MemberNotificationRepository memberNotificationRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final KafkaNotificationProducer kafkaNotificationProducer;
    private final EmailService emailService;
    private final CoolsmsService coolsmsService;

    /**
     * 관심 상품 등록 후 알림 전송을 비동기적으로 처리합니다.
     * (등록 알림: 기본적으로 알림 설정이 없으면 EMAIL로 발송)
     *
     * @param interestProductId 등록된 관심 상품의 ID
     * @param request           관심 상품 등록 요청 DTO (상품 ID 등 포함)
     * @param memberId          현재 로그인한 회원의 식별자 (이메일 주소로 사용)
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void dispatchNotificationForInterestProduct(Long interestProductId, InterestProductRequestDto request, String memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Product product = productRepository.findProductWithStockStatusById(request.getProductId());
        String productName = product.getProductName();
        String productUrl = product.getProductUrl();

        // 관심 상품 등록 완료 알림을 위한 이메일 템플릿 생성 (registration 타입)
        String htmlContent = emailService.generateEmailContent("registration", productName, productUrl, "");

        List<NotificationChannel> channels = getActiveChannels(memberId);

        NotificationEvent event = NotificationEvent.builder()
                .interestProductId(interestProductId)
                .emailAddress(member.getMemberId())   // memberId가 실제 이메일 주소라고 가정
                .phoneNumber(null)                     // 필요시 추가 정보 할당
                .discordUserId(null)               // 필요시 추가 정보 할당
                .messageContent(htmlContent)           // 기본 메시지 내용(템플릿 활용)
                .channels(channels)
                // 추가 정보 설정
                .notificationType("registration")
                .productName(productName)
                .productUrl(productUrl)
                .stockStatus("")                       // 등록 알림에서는 재고 상태가 없으므로 빈 문자열
                .build();

        kafkaNotificationProducer.sendNotification(event);
        log.info("관심 상품 등록 알림 이벤트 비동기 전송 완료: {}", event);
    }

    /**
     * 재고 상태 변경 시 알림 이벤트를 전송합니다.
     * (DB에 저장된 활성화된 알림 설정에 따라 전송)
     *
     * @param memberId         회원 식별자 (예: 이메일)
     * @param interestProductId 관심 상품 ID
     * @param messageContent   재고 상태 변경 알림 메시지 내용
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendNotificationEvent(String memberId, Long interestProductId, String messageContent, String stockStatus, String notificationType, String productName, String productUrl) {
        List<NotificationChannel> channels = getActiveChannels(memberId);

        NotificationEvent event = NotificationEvent.builder()
                .interestProductId(interestProductId)
                .emailAddress(memberId)
                .messageContent(messageContent)
                .channels(channels)
                // 추가 정보 설정
                .notificationType(notificationType)
                .productName(productName)
                .productUrl(productUrl)
                .stockStatus(stockStatus)
                .build();

        kafkaNotificationProducer.sendNotification(event);
        log.info("재고 변경 알림 이벤트 전송: {}", event);
    }

    /**
     * 회원의 활성화된 알림 채널 목록을 조회합니다.
     * 설정이 없거나 활성 채널이 없으면 기본 EMAIL 채널을 반환합니다.
     *
     * @param memberId 회원 식별자 (예: 이메일)
     * @return 활성화된 NotificationChannel 목록
     */
    private List<NotificationChannel> getActiveChannels(String memberId) {
        List<NotificationSetting> settings = notificationSettingRepository.findByMember_MemberId(memberId);
        List<NotificationChannel> channels = new ArrayList<>();
        if (settings == null || settings.isEmpty()) {
            channels.add(NotificationChannel.EMAIL);
        } else {
            for (NotificationSetting setting : settings) {
                if (setting.isEnabled()) {
                    channels.add(setting.getChannel());
                }
            }
            if (channels.isEmpty()) {
                channels.add(NotificationChannel.EMAIL);
            }
        }
        return channels;
    }

    /**
     * 관심 상품 알림 삭제
     *
     * @param interestProductId 관심 상품 ID
     * @param memberId          회원 식별자
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
     * 사용자 알림 채널 설정 업데이트: 기존 설정을 삭제하고, DTO 값에 따라 새롭게 저장합니다.
     *
     * @param memberId    회원 식별자
     * @param settingsDto 알림 설정 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSettings(String memberId, NotificationSettingsDto settingsDto) {
        List<NotificationSetting> existingSettings = notificationSettingRepository.findByMember_MemberId(memberId);
        if (existingSettings != null && !existingSettings.isEmpty()) {
            notificationSettingRepository.deleteAll(existingSettings);
        }
        Member member = memberRepository.findByMemberId(memberId);
        List<NotificationSetting> newSettings = new ArrayList<>();
        newSettings.add(NotificationSetting.builder()
                .member(member)
                .channel(NotificationChannel.EMAIL)
                .enabled(settingsDto.isEmailNotification())
                .build());
        newSettings.add(NotificationSetting.builder()
                .member(member)
                .channel(NotificationChannel.SMS)
                .enabled(settingsDto.isSmsNotification())
                .build());
        newSettings.add(NotificationSetting.builder()
                .member(member)
                .channel(NotificationChannel.DISCORD)
                .enabled(settingsDto.isDiscordNotification())
                .build());
        notificationSettingRepository.saveAll(newSettings);
        log.info("사용자 {}의 알림 설정 업데이트 완료: {}", memberId, settingsDto);
    }

    /**
     * 회원의 알림 설정을 조회하여 NotificationSettingsDto로 반환합니다.
     * 설정이 없는 경우 기본값(false)을 적용합니다.
     *
     * @param memberId 회원 식별자 (예: 이메일)
     * @return NotificationSettingsDto
     */
    @Transactional(readOnly = true)
    public NotificationSettingsDto getNotificationSettings(String memberId) {
        List<NotificationSetting> settings = notificationSettingRepository.findByMember_MemberId(memberId);
        boolean emailNotification = false;
        boolean smsNotification = false;
        boolean discordNotification = false;
        if (settings != null && !settings.isEmpty()) {
            for (NotificationSetting setting : settings) {
                if (setting.getChannel() == NotificationChannel.EMAIL) {
                    emailNotification = setting.isEnabled();
                } else if (setting.getChannel() == NotificationChannel.SMS) {
                    smsNotification = setting.isEnabled();
                } else if (setting.getChannel() == NotificationChannel.DISCORD) {
                    discordNotification = setting.isEnabled();
                }
            }
        }
        NotificationSettingsDto dto = NotificationSettingsDto.builder()
                .emailNotification(emailNotification)
                .smsNotification(smsNotification)
                .discordNotification(discordNotification)
                .build();
        log.info("회원 {}의 알림 설정 조회 결과: {}", memberId, dto);
        return dto;
    }
}
