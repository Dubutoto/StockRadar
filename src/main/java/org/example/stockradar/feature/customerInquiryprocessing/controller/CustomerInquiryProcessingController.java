package org.example.stockradar.feature.customerInquiryprocessing.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingRequestDto;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingResponseDto;
import org.example.stockradar.feature.customerInquiryprocessing.service.CustomerInquiryProcessingService;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.AuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("customerInquiryprocessing")
@RequiredArgsConstructor
public class CustomerInquiryProcessingController {
    private final CustomerInquiryProcessingService service;

    @GetMapping("customerInquiryprocessing")
    public String getCustomerInquiryProcessing() {
        return "customerInquiryprocessing/customerInquiryprocessing";
    }


    @GetMapping("api/customerInquiryprocessing")
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


    @PutMapping("processionCompleted/{inquiryId}")
    public String processionCompleted(
            @PathVariable Long inquiryId,
            @RequestBody CustomerInquiryProcessingRequestDto requestDto,
            Authentication authentication,
            Model model) {

        // 인증여부 확인
        if (authentication == null) {
            AuthException.throwAuthException(ErrorCode.UNAUTHORIZED);
            return null;
        }

        // 어드민 확인
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            AuthException.throwAuthException(ErrorCode.FORBIDDEN);
            return null;
        }

        // 문의 처리 완료
        service.processionCompleted(requestDto, inquiryId);

        // 처리 완료 후 최신 문의 목록 다시 조회
        List<CustomerInquiryProcessingResponseDto> inquiries = service.searchStatus();

        // 모델에 데이터 추가
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("inquiryCount", inquiries.size());
        model.addAttribute("message", "문의 처리가 완료되었습니다.");

        // 동일한 페이지로 리턴
        return "customerInquiryprocessing/customerInquiryprocessing";
    }




}
