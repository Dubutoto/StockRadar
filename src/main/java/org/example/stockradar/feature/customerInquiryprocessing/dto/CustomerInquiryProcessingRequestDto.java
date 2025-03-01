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
public class CustomerInquiryProcessingRequestDto {
    private String processingTitle;
    private String processingContent;
}
