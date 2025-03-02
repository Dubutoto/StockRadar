package org.example.stockradar.feature.customerInquiryprocessing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.CustomerInquiry.repository.CustomerInquiryRepository;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingRequestDto;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingResponseDto;
import org.example.stockradar.feature.customerInquiryprocessing.entity.CustomerInquiryProcessiong;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.CustomerInquiryException;
import org.example.stockradar.global.exception.specific.CustomerInquiryProcessiongException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerInquiryProcessingService {
    private final CustomerInquiryRepository repository;
    private final MemberRepository memberRepository;


    public List<CustomerInquiryProcessingResponseDto> searchStatus() {
        // 상태 0인 고객 문의 목록 조회
        List<CustomerInquiry> inquiries = repository.findByInquiryStatus(0);
        long count = inquiries.size();

        // 로그 출력: 전체 건수와 각 문의의 세부 정보를 출력
        log.info("상태가 0인 고객문의 수: {}", count);
        inquiries.forEach(inquiry ->
                log.info("문의 ID: {}, 제목: {}, 작성자: {}", inquiry.getInquiryId(), inquiry.getInquiryTitle(), inquiry.getMember())
        );

        // 조회된 결과가 없으면 예외 처리
        if (count == 0) {
            // 예외처리 작성
            CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
        }

        // Entity를 DTO로 변환하여 반환
        return inquiries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * CustomerInquiry 엔티티를 CustomerInquiryProcessingResponseDto로 변환
     */
    private CustomerInquiryProcessingResponseDto convertToDto(CustomerInquiry inquiry) {
        try {
            return CustomerInquiryProcessingResponseDto.builder()
                    .inquiryId(inquiry.getInquiryId())
                    .inquiryTitle(inquiry.getInquiryTitle())
                    .inquiryCategory(inquiry.getInquiryCategory())
                    .inquiryContent(inquiry.getInquiryContent())
                    .inquiryStatus(inquiry.getInquiryStatus())
                    .createdAt(inquiry.getCreatedAt())
                    .memberEmail(inquiry.getMember() != null ? inquiry.getMember().getMemberId() : "Unknown")
                    .build();
        } catch (Exception e) {
            CustomerInquiryProcessiongException.throwCustomException(ErrorCode.DATA_CONVERSION_ERROR);
            return null;
        }
    }


    public void processionCompleted(CustomerInquiryProcessingRequestDto requestDto, Long inquiryId) {
        // 1. 문의 조회 - Optional에서 값을 가져온 후 없으면 예외 처리
        Optional<CustomerInquiry> optionalInquiry = repository.findById(inquiryId);
        if (optionalInquiry.isEmpty()) {
            CustomerInquiryException.throwCustomException(ErrorCode.INQUIRY_NOT_FOUND);
        }

        CustomerInquiry inquiry = optionalInquiry.get();
        inquiry.setInquiryStatus(1);
        repository.save(inquiry);


        // 3. 문의 처리 엔티티 생성 및 저장
        try {
            CustomerInquiryProcessiong processing = CustomerInquiryProcessiong.builder()
                    .processingTitle(requestDto.getProcessingTitle())
                    .processingContent(requestDto.getProcessingContent())
                    .customerInquiry(inquiry)
                    .build();

            // 4. 문의 처리 엔티티 저장 (JPA 관계 매핑으로 인해 별도 저장 필요)
            inquiry.setCustomerInquiryProcessiong(processing);
            repository.save(inquiry);

            log.info("문의 ID: {}, 제목: {} 처리 완료", inquiry.getInquiryId(), inquiry.getInquiryTitle());
        } catch (Exception e) {
            CustomerInquiryException.throwCustomException(ErrorCode.RESOURCE_SAVE_FAILED);

        }

    }

}
