package com.lbg0146.crew_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "refresh";

    public void save(String loginId, String refreshToken, long ttlMs) {
        redisTemplate.opsForValue().set(PREFIX + loginId, refreshToken, ttlMs, TimeUnit.MILLISECONDS);
    }

    public String get(String loginId) {
        return redisTemplate.opsForValue().get(PREFIX + loginId);
    }

    public void delete(String loginId) {
        redisTemplate.delete(PREFIX + loginId);
    }

    public boolean isValid(String username, String refreshToken) {
        String stored  = get(username);
        return stored != null && stored.equals(refreshToken);
    }
}
