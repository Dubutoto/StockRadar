package org.example.stockradar.feature.customerInquiryprocessing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryResponseDto;
import org.example.stockradar.feature.auth.service.CustomUserDetailsService;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingRequestDto;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingResponseDto;
import org.example.stockradar.feature.customerInquiryprocessing.service.CustomerInquiryProcessingService;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.AuthException;
import org.example.stockradar.global.exception.specific.CustomerInquiryException;
import org.example.stockradar.global.exception.specific.CustomerInquiryProcessiongException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("customerInquiryprocessing")
@RequiredArgsConstructor
public class CustomerInquiryProcessingController {
    private final CustomerInquiryProcessingService service;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("customerInquiryprocessing")
    public String getCustomerInquiryProcessing() {
        return "customerInquiryprocessing/customerInquiryprocessing";
    }

    @GetMapping("api/customerInquiryprocessing")
    public ResponseEntity<?> getApiCustomerInquiryProcessing() {
//        log.info("고객문의처리요청");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            AuthException.throwAuthException(ErrorCode.UNAUTHORIZED);
        }

        String memberId = auth.getName();
        String role = customUserDetailsService.getMemberRole(memberId);

        if (!"ADMIN" .equals(role)) {
            AuthException.throwAuthException(ErrorCode.FORBIDDEN);
        }

        try {
            List<CustomerInquiryProcessingResponseDto> responses = service.getInqueryList();
//            log.info("고객문의 {} 개 조회 완료", responses.size());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
//            log.error("고객문의 조회 중 오류 발생: {}", e.getMessage());
            CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
            return null; // 이 코드는 실행되지 않지만 컴파일을 위해 필요합니다
        }
    }

    @GetMapping("detail/{inquiryId}")
    public String getCustomerInquiryDetail() {

        // 고정된 템플릿 경로 반환
        return "customerInquiryprocessing/detail";
    }

    @GetMapping("api/detail/{inquiryId}")
    public ResponseEntity<?> getApiCustomerInquiryDetail(@PathVariable Long inquiryId) {
//        log.info("고객문의 상세 정보 API 요청: {}", inquiryId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            AuthException.throwAuthException(ErrorCode.UNAUTHORIZED);
        }

        String memberId = auth.getName();
        String role = customUserDetailsService.getMemberRole(memberId);

        if (!"ADMIN" .equals(role)) {
            AuthException.throwAuthException(ErrorCode.FORBIDDEN);
        }

        try {
            CustomerInquiryProcessingResponseDto response = service.getInquiryById(inquiryId);
//            log.info("고객문의 상세 정보 조회 완료: {}", inquiryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            log.error("고객문의 상세 조회 중 오류 발생: {}", e.getMessage());
            CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
            return null; // 이 코드는 실행되지 않지만 컴파일을 위해 필요합니다
        }
    }

    @PostMapping("process")
    public String processCustomerInquiry(
            @RequestParam("inquiryId") Long inquiryId,
            @RequestParam("processingTitle") String processingTitle,
            @RequestParam("processingContent") String processingContent) {

//        log.info("고객문의 처리 요청: inquiryId={}, title={}", inquiryId, processingTitle);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            AuthException.throwAuthException(ErrorCode.UNAUTHORIZED);
        }

        String adminId = auth.getName();
        String role = customUserDetailsService.getMemberRole(adminId);

        if (!"ADMIN".equals(role)) {
            AuthException.throwAuthException(ErrorCode.FORBIDDEN);
        }

        try {
            // 문의 처리 서비스 호출
            CustomerInquiryProcessingRequestDto requestDto = CustomerInquiryProcessingRequestDto.builder()
                    .processingTitle(processingTitle)
                    .processingContent(processingContent)
                    .build();

            service.processInquiry(inquiryId, requestDto);

            // 성공 메시지와 함께 목록 페이지로 리다이렉트
            return "redirect:/customerInquiryprocessing/customerInquiryprocessing?success=true";
        } catch (Exception e) {
//            log.error("고객문의 처리 중 오류 발생: {}", e.getMessage());
            CustomerInquiryProcessiongException.throwCustomException(ErrorCode.INQUIRY_PROCESSING_FAILED);
            return null; // 이 코드는 실행되지 않지만 컴파일을 위해 필요합니다
        }
    }




}
