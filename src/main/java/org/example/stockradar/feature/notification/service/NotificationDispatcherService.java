package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.CoolsmsService;
import org.example.stockradar.feature.notification.dto.InterestProductRequestDto;
import org.example.stockradar.feature.notification.dto.NotificationEvent;
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author Hyun7en
 */

//분산해서 알림 발송
@Service
@RequiredArgsConstructor
public class NotificationDispatcherService {

    private final CoolsmsService coolSMSService;
    private final EmailService emailService;
    private final DiscordService discordService;
    private final WebPushService webPushService;
    private final IntertestProductService intertestProductService;
    private final MemberRepository memberRepository;
    private final KafkaNotificationProducer kafkaNotificationProducer;

    public void registerInterestProductAndDispatchNotification(InterestProductRequestDto request,String memberId) {
        // 관심 상품 등록 (비즈니스 로직은 InterestProductService로 위임)
        // 현재 로그인한 회원 정보 (또는 요청에서 가져온 회원 정보)
        Member member = memberRepository.findByMemberId(memberId);

        //관심 상품 서비스에서 관심상품 추가
        Long interestProductId = intertestProductService.registerInterestProduct(request, member);

        // NotificationEvent 생성
        //첫 track시 회원가입한 이메일로만 알림 발송
        NotificationEvent event = NotificationEvent.builder()
                .interestProductId(interestProductId)
                .emailAddress(member.getMemberId())       // 이메일 주소
                .messageContent("관심 상품 등록이 완료되었습니다.")
                .build();

        // Kafka 프로듀서를 통해 이벤트 발행(비동기 처리)
        kafkaNotificationProducer.sendNotification(event);
    }

    public void dispatchNotification(NotificationEvent event) {
        List<NotificationChannel> channels = event.getChannels();
        if (channels == null || channels.isEmpty()) {
            // 채널이 지정되지 않은 경우 기본 채널(이메일)로 발송
            emailService.sendEmail(event.getEmailAddress(), "알림", event.getMessageContent());
            return;
        }
        for (NotificationChannel channel : channels) {
            switch (channel) {
                case SMS:
                    coolSMSService.sendSms(event.getPhoneNumber(), event.getMessageContent());
                    break;
                case EMAIL:
                    emailService.sendEmail(event.getEmailAddress(), "알림", event.getMessageContent());
                    break;
                case DISCORD:
                    discordService.sendDirectMessage(event.getDiscordWebhookUrl(), event.getMessageContent());
                    break;
                case WEB_PUSH:
                    webPushService.sendWebPush(event.getInterestProductId(), event.getMessageContent());
                    break;
                default:
                    break;
            }
        }
    }
}
