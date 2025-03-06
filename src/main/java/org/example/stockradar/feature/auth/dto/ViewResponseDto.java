package org.example.stockradar.feature.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewResponseDto {
    private String memberId;
    private String role;
    private String message;
    private String hint;
    private Integer status;
    private String redirectUrl;
}
