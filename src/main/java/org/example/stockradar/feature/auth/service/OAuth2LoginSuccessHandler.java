package org.example.stockradar.feature.auth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.jwt.JwtTokenProvider;
import org.example.stockradar.feature.auth.jwt.RefreshTokenRedisService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1) OAuth2User
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2) 모든 소셜(Naver/Discord)에서 "memberId"를 넣어두었으므로
        String memberId = (String) attributes.get("memberId");
        if (memberId == null) {
            response.sendRedirect("/login?error=NoMemberId");
            return;
        }

        // 3) JWT 생성
        String accessToken = jwtTokenProvider.generateAccessToken(memberId);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        String sessionId = UUID.randomUUID().toString(); // 세션 ID 생성

        // 4) Redis에 RefreshToken 저장 (SESSION_ID 기반)
        refreshTokenRedisService.saveRefreshToken(sessionId, refreshToken, jwtTokenProvider.getRefreshTokenValidity());

        // 5) RefreshToken을 쿠키에서 제거하고, 대신 SESSION_ID를 쿠키에 저장
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");

        Cookie sessionCookie = new Cookie("SESSION_ID", sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(sessionCookie);

        // 6) 메인 페이지로 리다이렉트
        response.sendRedirect("/");
    }
}
