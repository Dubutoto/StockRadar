package org.example.stockradar.CustomerInquiryProcessing;

import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.CustomerInquiry.repository.CustomerInquiryRepository;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingRequestDto;
import org.example.stockradar.feature.customerInquiryprocessing.service.CustomerInquiryProcessingService;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerInquiryProcessingServiceTest {

    @Autowired
    private CustomerInquiryRepository repository;

    @Autowired
    private CustomerInquiryProcessingService service;

    private CustomerInquiry inquiry;
    private CustomerInquiryProcessingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        inquiry = CustomerInquiry.builder()
                .inquiryTitle("테스트 제목")
                .inquiryCategory("기술 지원")
                .inquiryContent("테스트 문의 내용입니다.")
                .inquiryStatus(0)
                .createdAt(LocalDateTime.now())
                .build();

        // DB에 저장
        inquiry = repository.save(inquiry);

        requestDto = new CustomerInquiryProcessingRequestDto();
        requestDto.setProcessingTitle("답변 제목");
        requestDto.setProcessingContent("답변 내용입니다.");
    }

    @Test
    @DisplayName("문의 처리 완료 - 성공 케이스")
    void processionCompleted_Success() {
        // when
        service.processionCompleted(requestDto, inquiry.getInquiryId());

        // then
        CustomerInquiry updatedInquiry = repository.findById(inquiry.getInquiryId())
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));

        assertEquals(1, updatedInquiry.getInquiryStatus()); // 상태가 1로 변경되었는지 확인
        assertNotNull(updatedInquiry.getCustomerInquiryProcessiong()); // 처리 엔티티가 생성되었는지 확인
        assertEquals("답변 제목", updatedInquiry.getCustomerInquiryProcessiong().getProcessingTitle());
        assertEquals("답변 내용입니다.", updatedInquiry.getCustomerInquiryProcessiong().getProcessingContent());
    }





    @Test
    @DisplayName("문의 처리 완료 - 저장 중 예외 발생")
    void processionCompleted_SaveException() {
        // given: 저장 중 예외를 발생시키기 위해 잘못된 엔터티 설정
        inquiry.setCustomerInquiryProcessiong(null); // 강제로 잘못된 상태로 설정

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            service.processionCompleted(requestDto, inquiry.getInquiryId());
        });

        assertEquals(ErrorCode.RESOURCE_SAVE_FAILED.getErrorCode(), exception.getErrorCode());
    }
}
