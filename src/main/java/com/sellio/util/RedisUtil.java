package com.sellio.util;

import com.sellio.model.enums.DomainType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public Long initializeViewCount(UUID id, DomainType type) {
        String key = (type == DomainType.SHOP) ? "shop_view_count_" + id : "listing_view_count_" + id;
        Object viewCount = redisTemplate.opsForValue().get(key);
        if (viewCount == null) {
            redisTemplate.opsForValue().set(key, String.valueOf(1));
            viewCount = 1L;
        } else {
            long longViewCount = Long.parseLong(String.valueOf(viewCount)) + 1;
            redisTemplate.opsForValue().set(key, String.valueOf(longViewCount));
            viewCount = longViewCount;
        }
        return (Long) viewCount;
    }

    public Long getViewCount(UUID id, DomainType type) {
        String key = (type == DomainType.SHOP) ? "shop_view_count_" + id : "listing_view_count_" + id;
        Object viewCount = redisTemplate.opsForValue().get(key);
        return Long.parseLong(String.valueOf(viewCount));
    }
}
