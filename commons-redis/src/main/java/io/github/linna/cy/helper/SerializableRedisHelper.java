package io.github.linna.cy.helper;

import io.github.linna.cy.helper.impl.BaseRedisHelperImpl;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

public class SerializableRedisHelper<V> extends BaseRedisHelperImpl<Serializable, V> implements IRedisHelper<Serializable, V> {
    public SerializableRedisHelper(RedisTemplate<Serializable, V> template) {
        super(template);
    }
}
