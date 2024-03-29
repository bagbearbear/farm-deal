package com.farmdeal.global.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@RequiredArgsConstructor
@Component
public class TokenRepository {


    private final RedisTemplate<String, Object> redisTemplate;

    public void saveToken(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    public String getToken(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public boolean isTokenBlacklisted(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }
}
