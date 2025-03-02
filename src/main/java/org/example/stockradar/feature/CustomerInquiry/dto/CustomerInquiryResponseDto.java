package org.example.stockradar.feature.CustomerInquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInquiryResponseDto {
    private Long inquiryId;
    private String message;
    private String hint;
    private Integer status;
    private String redirectUrl;
}
