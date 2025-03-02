package org.example.stockradar.CustomerInquiryProcessing;

import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.CustomerInquiry.repository.CustomerInquiryRepository;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingRequestDto;
import org.example.stockradar.feature.customerInquiryprocessing.service.CustomerInquiryProcessingService;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.CustomerInquiryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CustomerInquiryProcessingServiceSelectTest {

    @Autowired
    private CustomerInquiryProcessingService customerInquiryProcessingService;

    @Autowired
    private CustomerInquiryRepository customerInquiryRepository;

    @Test
    @DisplayName("문의 ID로 조회 시 문의가 없을 경우 예외 발생")
    public void processionCompleted_WhenInquiryNotFound_ThrowsException() {
        // given
        Long nonExistentInquiryId = 99999L; // 존재하지 않는 ID 사용
        CustomerInquiryProcessingRequestDto requestDto = new CustomerInquiryProcessingRequestDto();
        requestDto.setProcessingTitle("처리 제목");
        requestDto.setProcessingContent("처리 내용");

        // when & then
        try {
            customerInquiryProcessingService.processionCompleted(requestDto, nonExistentInquiryId);
            fail("예외가 발생해야 합니다.");
        } catch (CustomException e) {
            // 예외가 발생했을 때의 검증
            assertEquals(ErrorCode.INQUIRY_NOT_FOUND.getDescription(), e.getErrorCode());
            System.out.println("예외 발생: " + e.getErrorMessage());
        }
    }

    @Test
    @DisplayName("상태가 0인 문의가 없을 경우 예외 발생")
    public void searchStatus_WhenNoInquiriesWithStatusZero_ThrowsException() {
        // 모든 문의의 상태를 1로 변경하여 상태가 0인 문의가 없도록 설정
        customerInquiryRepository.findByInquiryStatus(0)
                .forEach(inquiry -> {
                    inquiry.setInquiryStatus(1);
                    customerInquiryRepository.save(inquiry);
                });

        // when & then
        try {
            customerInquiryProcessingService.searchStatus();
            fail("예외가 발생해야 합니다.");
        } catch (CustomException e) {
            // 예외가 발생했을 때의 검증
            assertEquals(ErrorCode.INQUIRY_NOT_FOUND.getErrorCode(), e.getErrorCode());
            System.out.println("예외 발생: " + e.getErrorMessage());
        }
    }
}
