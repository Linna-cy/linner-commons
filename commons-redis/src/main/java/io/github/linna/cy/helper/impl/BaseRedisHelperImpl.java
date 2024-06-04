package io.github.linna.cy.helper.impl;

import io.github.linna.cy.helper.IRedisHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@RequiredArgsConstructor
public class BaseRedisHelperImpl<K, V> implements IRedisHelper<K, V> {
    private final RedisTemplate<K, V> template;

    /**
     * 在调用以 set / put 开头的方法前, 对 {@code key} 进行验证
     *
     * @return 当返回值为 {@code true} 时, 以 set 开头的方法可以正常调用;
     * 当返回值为 {@code false} 时, 以 set 开头的方法将抛出 {@link IllegalArgumentException} 异常
     */
    protected boolean verifyKeyBeforeSet(K key) {
        return key != null;
    }

    private void verifyKey(K key) {
        if (!verifyKeyBeforeSet(key)) {
            log.error("IllegalArgumentException: The key of redis can't pass verification.");
            throw new IllegalArgumentException("The key of redis can't pass verification.");
        }
    }

    @Override
    public IRedisHelper<K, V> set(K key, V value) {
        verifyKey(key);
        return IRedisHelper.super.set(key, value);
    }

    @Override
    public boolean setWithExpirationAt(K key, V value, Date expiration) {
        verifyKey(key);
        return IRedisHelper.super.setWithExpirationAt(key, value, expiration);
    }

    @Override
    public boolean setWithExpirationAt(K key, V value, Instant expiration) {
        verifyKey(key);
        return IRedisHelper.super.setWithExpirationAt(key, value, expiration);
    }

    @Override
    public boolean setWithExpiration(K key, V value, long expiration) {
        verifyKey(key);
        return IRedisHelper.super.setWithExpiration(key, value, expiration);
    }

    @Override
    public boolean setWithExpiration(K key, V value, long expiration, TimeUnit timeUnit) {
        verifyKey(key);
        return IRedisHelper.super.setWithExpiration(key, value, expiration, timeUnit);
    }

    @Override
    public IRedisHelper<K, V> put(K key, Serializable field, Object value) {
        verifyKey(key);
        return IRedisHelper.super.put(key, field, value);
    }

    @Override
    public IRedisHelper<K, V> put(K key, Map<Serializable, ?> map) {
        verifyKey(key);
        return IRedisHelper.super.put(key, map);
    }

    @Override
    public boolean putIfAbsent(K key, Serializable field, Object value) {
        verifyKey(key);
        return IRedisHelper.super.putIfAbsent(key, field, value);
    }
}
