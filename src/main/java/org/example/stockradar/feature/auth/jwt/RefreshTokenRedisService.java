package org.example.stockradar.feature.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 저장
    public void saveRefreshToken(String memberId, String refreshToken, long validityMillis) {
        String key = "RT:" + memberId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(validityMillis));
    }

    // 조회
    public String getRefreshToken(String memberId) {
        return redisTemplate.opsForValue().get("RT:" + memberId);
    }

    // 삭제, 로그아웃 용
    public void deleteRefreshToken(String memberId) {
        redisTemplate.delete("RT:" + memberId);
    }
}

