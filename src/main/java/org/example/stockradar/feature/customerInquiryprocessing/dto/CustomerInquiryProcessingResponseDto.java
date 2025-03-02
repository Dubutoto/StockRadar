package org.example.stockradar.feature.customerInquiryprocessing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInquiryProcessingResponseDto {
    private Long inquiryId;
    private String inquiryTitle;
    private String inquiryCategory;
    private String inquiryContent;
    private Integer inquiryStatus;
    private LocalDateTime createdAt;
    private String memberEmail; // 문의 작성자 이름

}
