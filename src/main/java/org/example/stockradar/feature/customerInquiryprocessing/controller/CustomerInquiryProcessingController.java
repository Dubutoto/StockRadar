package org.example.stockradar.feature.customerInquiryprocessing.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingResponseDto;
import org.example.stockradar.feature.customerInquiryprocessing.service.CustomerInquiryProcessingService;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.AuthException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("customerInquiryprocessing")
@RequiredArgsConstructor
public class CustomerInquiryProcessingController {
    private final CustomerInquiryProcessingService service;


    @GetMapping("customerInquiryprocessing")
    public String customerInquiryProcessing(Authentication authentication, Model model) {

        //인증 여부 확인
        if (authentication == null) {
            AuthException.throwAuthException(ErrorCode.UNAUTHORIZED);
            return null;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        //어드민인지 검증 예외처리
        if (!isAdmin) {
            AuthException.throwAuthException(ErrorCode.FORBIDDEN);
            return null;
        }

        // 상태가 0인 고객문의 목록 조회
        List<CustomerInquiryProcessingResponseDto> inquiries = service.searchStatus();

        // 모델에 데이터 추가
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("inquiryCount", inquiries.size());

        return "customerInquiryprocessing/customerInquiryprocessing";
    }



}
