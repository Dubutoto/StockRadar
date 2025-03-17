package org.example.stockradar.feature.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.dto.CommentResponseDto;
import org.example.stockradar.feature.notification.dto.InterestProductRequestDto;
import org.example.stockradar.feature.notification.dto.InterestProductResponseDto;
import org.example.stockradar.feature.notification.service.IntertestProductService;
import org.example.stockradar.feature.notification.service.NotificationDispatcherService;
import org.example.stockradar.feature.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final IntertestProductService intertestProductService;

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
        return ResponseEntity.ok("관심 상품 등록 및 기본 알림 설정 완료");
    }

    /**
     * 관심 상품 조회
     * 예시: 특정 회원의 관심 상품 목록을 조회합니다.
     */
    @GetMapping("read")
    public ResponseEntity<Page<InterestProductResponseDto>> getInterestProducts(Authentication authentication, @RequestParam(defaultValue = "0") int page) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("페이지를 불러올 수 없음");
        }
        // 인증된 사용자 ID 가져오기 (예: JWT의 subject를 사용하여 memberId 반환)
        String memberId = String.valueOf(authentication.getName());
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<InterestProductResponseDto> interestProductPage = intertestProductService.getInterestProductsByMemberId(memberId,pageable);

        return ResponseEntity.ok(interestProductPage);
    }

    /**
     * 관심 상품 삭제 + 알림 삭제
     * 예시: 관심 상품 삭제 시 해당 관심 상품과 관련된 알림도 함께 삭제합니다.
     */
    @PostMapping("delete")
    public ResponseEntity<String> deleteInterestProduct(@RequestBody InterestProductRequestDto request, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 해주세요.");
        }
        String memberId = String.valueOf(authentication.getName());

        Long interestProductId = intertestProductService.deleteInterestProduct(request.getProductId(), memberId);
        // 필요에 따라, 관심 상품과 연관된 알림도 삭제 처리
        notificationService.deleteNotificationsByInterestProductId(interestProductId,memberId);
        return ResponseEntity.ok("관심 상품 및 관련 알림 삭제 완료");
    }

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
