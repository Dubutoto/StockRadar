package org.example.stockradar.feature.CustomerInquiry.controller;

import jakarta.persistence.EntityGraph;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryResponseDto;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryUserRequestDto;
import org.example.stockradar.feature.CustomerInquiry.service.CustomerInquiryService;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.AuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("customerInquiry")
@RequiredArgsConstructor
public class CustomerInquiryController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerInquiryController.class);
    private final CustomerInquiryService service;
    @GetMapping("customerInquiry")
    public String customerInquiryPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
//            logger.warn("인증 실패.");
            return "redirect:/login";
        }

        // 인증된 사용자 ID 가져오기
        String memberId = authentication.getName();
//        logger.info("Authenticated user ID: {}", memberId);
        model.addAttribute("memberId", memberId);
        // customerInquiry.html 뷰 템플릿을 반환
        return "/customerInquiry/customerInquiry";
    }


    @GetMapping("/api/check")
    public ResponseEntity<?> customerInquiry(Authentication authentication, Model model) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
//                logger.warn("인증 실패.");
                AuthException.throwAuthException(ErrorCode.UNAUTHORIZED);
            }

            // 인증된 사용자 ID 가져오기
            String memberId = authentication.getName();
//            logger.info("Authenticated user ID: {}", memberId);
            model.addAttribute("memberId", memberId);

            // 인증 성공 시 응답 - CustomerInquiryResponseDto 객체 사용
            CustomerInquiryResponseDto responseDto = CustomerInquiryResponseDto.builder()
                    .status(200)
                    .message("인증 성공")
                    .redirectUrl("/customerInquiry/customerInquiry")
                    .build();

            return ResponseEntity.ok(responseDto);

        } catch (CustomException e) {
            // 예외 발생 시 응답 - CustomerInquiryResponseDto 객체 사용
            CustomerInquiryResponseDto responseDto = CustomerInquiryResponseDto.builder()
                    .status(e.getHttpStatus())
                    .message(e.getErrorMessage())
                    .hint(e.getHint())
                    .redirectUrl("/login")
                    .build();

//            logger.error("인증 예외 발생: {}, 힌트: {}", e.getErrorMessage(), e.getHint());

            return ResponseEntity
                    .status(e.getHttpStatus())
                    .body(responseDto);
        }
    }


    @PostMapping("submit")
    public ResponseEntity<?> submitCustomerInquiry(
            @RequestBody CustomerInquiryUserRequestDto requestDto,
            Authentication authentication) {

        if (authentication == null) {
            throw new CustomException(
                    ErrorCode.UNAUTHORIZED.getErrorCode(),
                    ErrorCode.UNAUTHORIZED.getErrorMessage(),
                    ErrorCode.UNAUTHORIZED.getDescription(),
                    ErrorCode.UNAUTHORIZED.getHttpStatus()
            );
        }

        String memberId = authentication.getName();
        CustomerInquiryResponseDto responseDto = service.saveCustomerInquiry(requestDto, memberId);
        return ResponseEntity.ok(Map.of(
                "inquiryId", responseDto.getInquiryId(),
                "redirectUrl", "/customerInquiry/customerInquiry",
                "message", responseDto.getMessage()
        ));
    }


}
