package org.example.stockradar.feature.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String sessionId, String refreshToken, long validityMillis) {
        redisTemplate.opsForValue().set("REFRESH:" + sessionId, refreshToken, Duration.ofMillis(validityMillis));
    }

    public String getRefreshToken(String sessionId) {
        return redisTemplate.opsForValue().get("REFRESH:" + sessionId);
    }

    public void deleteRefreshToken(String sessionId) {
        redisTemplate.delete("REFRESH:" + sessionId);
    }
}
