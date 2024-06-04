package io.github.linna.cy.helper;

import org.springframework.data.redis.core.RedisTemplate;

public interface RedisBaseHelper<K, V> {
    RedisTemplate<K, V> getTemplate();
}
