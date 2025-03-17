package org.example.stockradar.feature.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String sessionId, String refreshToken, long validityMillis) {
        System.out.println("🔹 Redis에 RefreshToken 저장: SESSION_ID=" + sessionId + ", Token=" + refreshToken);
        redisTemplate.opsForValue().set("SESSION:" + sessionId, refreshToken, Duration.ofMillis(validityMillis));
    }

    public String getRefreshToken(String sessionId) {
        return redisTemplate.opsForValue().get("SESSION:" + sessionId);
    }

    public void deleteRefreshToken(String sessionId) {
        redisTemplate.delete("SESSION:" + sessionId);
    }
}


