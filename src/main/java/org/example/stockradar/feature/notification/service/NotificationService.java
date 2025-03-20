package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
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

/**
 * @author Hyun7en
 */

//알림 관련 로직 service
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // 메모리 내에서 현재 설정을 관리 (실제 환경에서는 캐시나 DB를 사용할 수 있음)
    private final NotificationSettingRepository notificationSettingRepository;
    private final MemberNotificationRepository memberNotificationRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final KafkaNotificationProducer kafkaNotificationProducer;
    private final EmailService emailService;

    /**
     * 관심 상품 등록 후 알림 전송을 비동기적으로 처리합니다.
     *
     * @param interestProductId 등록된 관심 상품의 ID
     * @param request           관심 상품 등록 요청 DTO (상품 ID 등 포함)
     * @param memberId          현재 로그인한 회원의 식별자 (이메일 주소로 사용)
     */
    @Async
    @Transactional(readOnly = true)
    public void dispatchNotificationForInterestProduct(Long interestProductId, InterestProductRequestDto request, String memberId) {
        // 회원 정보 조회 (필요하다면)
        Member member = memberRepository.findByMemberId(memberId);
        // 제품 정보 조회 (관심 상품 등록 시 사용한 productId 기준)
        Product product = productRepository.findProductWithStockStatusById(request.getProductId());
        String productName = product.getProductName();
        String productUrl = product.getProductUrl();

        // HTML 템플릿 로드 및 치환
        String htmlContent = emailService.loadHtmlTemplate(productName, productUrl);

        // 사용자의 알림 설정을 조회하여 활성 채널 목록 구성
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

        // NotificationEvent 생성
        NotificationEvent event = NotificationEvent.builder()
                .interestProductId(interestProductId)
                .emailAddress(member.getMemberId())  // memberId가 실제 이메일 주소라 가정
                .messageContent(htmlContent)
                .channels(channels)
                .build();

        // Kafka 프로듀서를 통해 이벤트 발행 (비동기 처리)
        kafkaNotificationProducer.sendNotification(event);
        log.info("알림 이벤트 비동기 전송 완료: {}", event);
    }

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
     * 회원의 알림 설정을 조회하여 NotificationSettingsDto로 반환합니다.
     * 회원의 설정이 없는 경우 기본값(false)으로 처리합니다.
     *
     * @param memberId 회원 식별에 사용되는 값 (실제 운영에서는 이메일 주소 등)
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
                // WEB_PUSH 등 추가 채널이 있을 경우, 이곳에 처리 로직 추가 가능
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
