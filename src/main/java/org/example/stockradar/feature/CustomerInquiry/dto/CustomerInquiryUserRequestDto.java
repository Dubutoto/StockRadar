package org.example.stockradar.feature.CustomerInquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInquiryUserRequestDto {
    private String title;       // 문의 제목
    private Integer category;    // 카테고리
    private String email;       // 이메일
    private String content;     // 문의 내용
}
