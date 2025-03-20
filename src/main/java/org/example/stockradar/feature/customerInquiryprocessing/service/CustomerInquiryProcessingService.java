package org.example.stockradar.feature.customerInquiryprocessing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryResponseDto;
import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.CustomerInquiry.repository.CustomerInquiryRepository;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.SmtpMailService;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingRequestDto;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingResponseDto;
import org.example.stockradar.feature.customerInquiryprocessing.entity.CustomerInquiryProcessiong;
import org.example.stockradar.feature.customerInquiryprocessing.repository.CustomerInquiryProcessingRepository;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.AuthException;
import org.example.stockradar.global.exception.specific.CustomerInquiryException;
import org.example.stockradar.global.exception.specific.CustomerInquiryProcessiongException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerInquiryProcessingService {
    public final CustomerInquiryRepository customerInquiryRepository;

    @Autowired
    private SmtpMailService mailService;

    @Autowired
    private CustomerInquiryProcessingRepository processingRepository;

    public List<CustomerInquiryProcessingResponseDto> getInqueryList() {
        try {
            // 상태가 0인 미처리 문의만 조회
            List<CustomerInquiry> inquiries = customerInquiryRepository.findByInquiryStatus(0);

            return inquiries.stream()
                    .map(inquiry -> CustomerInquiryProcessingResponseDto.builder()
                            .inquiryId(inquiry.getInquiryId())
                            .inquiryTitle(inquiry.getInquiryTitle())
                            .inquiryCategory(inquiry.getInquiryCategory())
                            .inquiryStatus(inquiry.getInquiryStatus())
                            .createdAt(inquiry.getCreatedAt())
                            .memberEmail(inquiry.getMember() != null ? inquiry.getMember().getMemberId() : "Unknown")
                            .redirectUrl("/customerInquiryprocessing/customerInquiryprocessing")
                            .message("고객 문의 조회 성공")
                            .status(200)
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("고객 문의 목록 조회 중 오류 발생: {}", e.getMessage());
            CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
            return Collections.emptyList(); // 컴파일을 위해 필요합니다
        }
    }

    public CustomerInquiryProcessingResponseDto getInquiryById(Long inquiryId) {
        try {
            // 문의 ID로 엔티티 조회
            Optional<CustomerInquiry> inquiryOptional = customerInquiryRepository.findById(inquiryId);

            if (inquiryOptional.isEmpty()) {
                log.error("문의 ID {}에 해당하는 문의를 찾을 수 없습니다", inquiryId);
                CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
            }

            CustomerInquiry inquiry = inquiryOptional.get();

            // DTO로 변환하여 반환
            return CustomerInquiryProcessingResponseDto.builder()
                    .inquiryId(inquiry.getInquiryId())
                    .inquiryTitle(inquiry.getInquiryTitle())
                    .inquiryContent(inquiry.getInquiryContent())
                    .inquiryCategory(inquiry.getInquiryCategory())
                    .inquiryStatus(inquiry.getInquiryStatus())
                    .createdAt(inquiry.getCreatedAt())
                    .memberEmail(inquiry.getMember() != null ? inquiry.getMember().getMemberId() : "Unknown")
                    .redirectUrl("/customerInquiryprocessing/detail/" + inquiryId)
                    .message("고객 문의 상세 조회 성공")
                    .status(200)
                    .build();


        } catch (Exception e) {
            log.error("고객 문의 상세 조회 중 오류 발생: {}", e.getMessage());
            CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
            return null; // 컴파일을 위해 필요하지만 실행되지 않음
        }
    }

    @Transactional
    public void processInquiry(Long inquiryId, CustomerInquiryProcessingRequestDto requestDto) {
        try {
            // 문의 ID로 엔티티 조회
            Optional<CustomerInquiry> inquiryOptional = customerInquiryRepository.findById(inquiryId);

            if (inquiryOptional.isEmpty()) {
                log.error("문의 ID {}에 해당하는 문의를 찾을 수 없습니다", inquiryId);
                CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
            }

            CustomerInquiry inquiry = inquiryOptional.get();

            // 처리 상태가 아닌 경우에만 처리
            if (inquiry.getInquiryStatus() != 0) { // 0 = 미처리 상태라고 가정
                log.warn("이미 처리된 문의입니다: {}", inquiryId);
                // 이미 처리된 경우 로그만 남기고 계속 진행
            }

            // 문의 상태 업데이트 (1 = 처리 완료 상태로 변경)
            inquiry.setInquiryStatus(1);
            customerInquiryRepository.save(inquiry);

            // 처리 내역 저장
            CustomerInquiryProcessiong processing = CustomerInquiryProcessiong.builder()
                    .processingTitle(requestDto.getProcessingTitle())
                    .processingContent(requestDto.getProcessingContent())
                    .customerInquiry(inquiry)
                    .build();

            processingRepository.save(processing);

            // 이메일 전송
            String memberEmail = inquiry.getMember().getMemberId(); // 이메일 주소

            mailService.sendMail(
                    memberEmail,
                    requestDto.getProcessingTitle(),
                    requestDto.getProcessingContent()
            );

            log.info("고객문의 처리 및 이메일 전송 완료: inquiryId={}, email={}", inquiryId, memberEmail);


        } catch (Exception e) {
            log.error("고객문의 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("고객문의 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
