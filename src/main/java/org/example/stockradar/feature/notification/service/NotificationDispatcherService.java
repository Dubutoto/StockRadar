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
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final CoolsmsService coolSMSService;
    private final EmailService emailService;
    private final DiscordService discordService;
    private final WebPushService webPushService;
    private final IntertestProductService intertestProductService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final KafkaNotificationProducer kafkaNotificationProducer;

    @Value("classpath:templates/email-template/email-template1.html")
    private Resource emailTemplate;

    public void registerInterestProductAndDispatchNotification(InterestProductRequestDto request, String memberId) {
        // 현재 로그인한 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId);
        // 관심 상품 등록
        Long interestProductId = intertestProductService.registerInterestProduct(request, member);

        // 제품 정보를 조회 (관심 상품 등록 시 사용한 productId 사용)
        Product product = productRepository.findProductWithStockStatusById(request.getProductId());
        String productName = product.getProductName();
        String productUrl = product.getProductUrl();

        // HTML 템플릿 로드 및 데이터 치환: 제품 이름과 URL 치환
        String htmlContent = loadHtmlTemplate(productName, productUrl);

        // NotificationEvent 생성 (기본적으로 이메일 채널로 전송)
        NotificationEvent event = NotificationEvent.builder()
                .interestProductId(interestProductId)
                .emailAddress(member.getMemberId()) // 이메일 주소 예시로 memberId 사용
                .messageContent(htmlContent)
                .build();

        // Kafka 프로듀서를 통해 이벤트 발행 (비동기 처리)
        kafkaNotificationProducer.sendNotification(event);
    }

    private String loadHtmlTemplate(String productName, String productUrl) {
        try {
            String template = StreamUtils.copyToString(emailTemplate.getInputStream(), StandardCharsets.UTF_8);
            // 템플릿의 플레이스홀더를 실제 값으로 치환
            template = template.replace("{{productName}}", productName);
            template = template.replace("{{interestProductUrl}}", productUrl);
            return template;
        } catch (IOException e) {
            log.error("HTML 템플릿 로드 실패", e);
            return "";
        }
    }

    public void dispatchNotification(NotificationEvent event) {
        List<NotificationChannel> channels = event.getChannels();
        if (channels == null || channels.isEmpty()) {
            try {
                emailService.sendHtmlEmail(event.getEmailAddress(), "알림", event.getMessageContent());
            } catch (Exception e) {
                log.error("기본 채널(이메일) 전송 실패: {}", e.getMessage());
            }
            return;
        }
        for (NotificationChannel channel : channels) {
            try {
                switch (channel) {
                    case SMS:
                        coolSMSService.sendSms(event.getPhoneNumber(), event.getMessageContent());
                        break;
                    case EMAIL:
                        emailService.sendHtmlEmail(event.getEmailAddress(), "알림", event.getMessageContent());
                        break;
                    case DISCORD:
                        discordService.sendDirectMessage(event.getDiscordWebhookUrl(), event.getMessageContent());
                        break;
                    case WEB_PUSH:
                        webPushService.sendWebPush(event.getInterestProductId(), event.getMessageContent());
                        break;
                    default:
                        log.warn("알 수 없는 알림 채널: {}", channel);
                        break;
                }
            } catch (Exception e) {
                log.error("{} 채널 전송 실패: {}", channel, e.getMessage());
            }
        }
    }
}
