package org.example.stockradar.feature.CustomerInquiry.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryUserRequestDto;
import org.example.stockradar.feature.CustomerInquiry.service.CustomerInquiryService;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("customerInquiry")
@RequiredArgsConstructor
public class CustomerInquiryController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerInquiryController.class);
    private final CustomerInquiryService service;

    @GetMapping("customerInquiry")
    public String customerInquiry() {
        logger.info("GET request received for /customerInquiry");
        return "customerInquiry/customerInquiry";
    }

    @PostMapping("submit")
    public ResponseEntity<Long> submitCustomerInquiry(
            @RequestBody CustomerInquiryUserRequestDto requestDto,
            Authentication authentication) {

        if (authentication == null) {
            //예외처리 작성
            throw new CustomException(
                    ErrorCode.UNAUTHORIZED.getErrorCode(),
                    ErrorCode.UNAUTHORIZED.getErrorMessage(),
                    ErrorCode.UNAUTHORIZED.getDescription(),
                    ErrorCode.UNAUTHORIZED.getHttpStatus()
            );
        }

        // 인증된 사용자의 ID 가져오기
        String memberId = authentication.getName();
        logger.info("Authenticated user ID: {}", memberId);

        // 서비스 계층 호출
        Long inquiryId = service.saveCustomerInquiry(requestDto, memberId);
        return ResponseEntity.ok(inquiryId);
    }
}
