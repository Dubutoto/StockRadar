package org.example.stockradar.feature.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final String SECRET_KEY = "YOUR_SECRET_KEY_AtLeast_32chars_Long";
    private final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 30;       // 30분
    private final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24; // 1일

    private final Key key;

    public JwtTokenProvider() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(String memberId) {
        return Jwts.builder()
                .setSubject(memberId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString(); // RefreshToken을 예측 불가능한 UUID로 변경
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getMemberIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //  RefreshToken의 유효기간 반환 메서드 추가
    public long getRefreshTokenValidity() {
        return REFRESH_TOKEN_VALIDITY;
    }
}
