package org.example.stockradar.feature.auth.dto;

import lombok.Getter;
import lombok.Setter;

// 토큰 응답 DTO
@Getter
@Setter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse() {}

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
