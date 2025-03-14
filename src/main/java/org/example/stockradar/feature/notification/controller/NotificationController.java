package org.example.stockradar.feature.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.notification.dto.InterestProductRequestDto;
import org.example.stockradar.feature.notification.service.NotificationDispatcherService;
import org.example.stockradar.feature.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Hyun7en
 */

@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationDispatcherService notificationDispatcherService;

    /**
     * 관심 상품 등록 + 알림 전송
     * 예시: 사용자가 관심 상품을 등록하면 해당 정보를 저장하고,
     * 등록 완료 후 알림 이벤트를 생성하여 선택된 채널로 알림을 발송합니다.
     */
    @PostMapping("register")
    public ResponseEntity<String> registerInterestProduct(@RequestBody InterestProductRequestDto request, Authentication authentication) {

        System.out.println("Registering interest product: " + request);

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 해주세요.");
        }

        // 인증된 사용자 ID 가져오기 (예: JWT의 subject를 사용하여 memberId 반환)
        String memberId = String.valueOf(authentication.getName());

        // 관심상품 등록 및 알림 설정 처리 (서비스 계층에 위임)
        notificationDispatcherService.registerInterestProductAndDispatchNotification(request, memberId);

        // 성공 시 문자열 메시지 반환
        return ResponseEntity.ok("관심 상품 등록 및 알림 전송 완료");
    }

    /**
     * 관심 상품 조회
     * 예시: 특정 회원의 관심 상품 목록을 조회합니다.
     */
//    @GetMapping("")
//    public ResponseEntity<?> getInterestProducts(@RequestParam Long memberId) {
//        return ResponseEntity.ok(notificationService.getInterestProductsByMemberId(memberId));
//    }

    /**
     * 관심 상품 삭제 + 알림 삭제
     * 예시: 관심 상품 삭제 시 해당 관심 상품과 관련된 알림도 함께 삭제합니다.
     */
//    @PostMapping("delete/{interestProductId}")
//    public ResponseEntity<String> deleteInterestProduct(@PathVariable Long interestProductId) {
//        notificationService.deleteInterestProduct(interestProductId);
//        // 필요에 따라, 관심 상품과 연관된 알림도 삭제 처리
//        notificationService.deleteNotificationsByInterestProductId(interestProductId);
//        return ResponseEntity.ok("관심 상품 및 관련 알림 삭제 완료");
//    }

    /**
     * 알림 전송 (수동 트리거)
     * 예시: 특정 NotificationEvent 정보를 받아 알림을 전송합니다.
     */
//    @PostMapping("/dispatch")
//    public ResponseEntity<String> dispatchNotification(@RequestBody NotificationEvent event) {
//        notificationDispatcherService.dispatchNotification(event);
//        return ResponseEntity.ok("알림 전송 완료");
//    }

}
