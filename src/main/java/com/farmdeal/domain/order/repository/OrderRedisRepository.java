package com.farmdeal.domain.order.repository;

import com.farmdeal.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OrderRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final String COUNT_KEY = "orderCount";

    public Long increment(Product product) {
        return redisTemplate.opsForValue().increment(COUNT_KEY+product.getId(), 1L);
    }

    public void decrement(Product product) {
        redisTemplate.opsForValue().decrement(COUNT_KEY + product.getId(), 1L);
    }
}
