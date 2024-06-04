package io.github.linna.cy.helper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.redis.connection.DataType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface IRedisHelper<K, V> extends RedisOperationsHelper<K, V> {

    /**
     * 过期时间 (Tome to Live, TTL) 为永不过期
     */
    long EXPIRE_PERPETUAL = -1;

    /**
     * 通过指定的参数 {@code key} 从 Redis 中获取值
     */
    default V get(K key) {
        return this.opsForValue().get(key);
    }

    /**
     * 为指定的参数 {@code key} 设置 {@code value}
     */
    default IRedisHelper<K, V> set(K key, V value) {
        this.opsForValue().set(key, value);
        return this;
    }

    /**
     * 设置键的同时为其设置 过期时间点
     *
     * @param expiration 过期时间点. 这个时间点必须在当前时间之后, 否则参数 {@code value} 将不会被设置.
     */
    default boolean setWithExpirationAt(K key, V value, @NotNull Date expiration) {
        return setWithExpirationAt(key, value, expiration.toInstant());
    }

    /**
     * 设置键的同时为其设置 过期时间点
     *
     * @param expiration 过期时间点. 这个时间点必须在当前时间之后, 否则参数 {@code value} 将不会被设置, 并返回 {@code false}.
     */
    default boolean setWithExpirationAt(K key, V value, @NotNull Instant expiration) {
        Duration duration = Duration.between(new Date().toInstant(), expiration);
        if (duration.isNegative()) {
            return false;
        }
        this.opsForValue().set(key, value, duration);
        return true;
    }

    /**
     * 设置键的同时为其设置 过期时间
     *
     * @param expiration 过期时间 (单位: ms)
     */
    default boolean setWithExpiration(K key, V value, long expiration) {
        return setWithExpiration(key, value, expiration, TimeUnit.MINUTES);
    }

    /**
     * 设置键的同时为其设置 过期时间
     *
     * @param expiration 过期时间
     * @param timeUnit   过期时间的单位
     */
    default boolean setWithExpiration(K key, V value, long expiration, @NotNull TimeUnit timeUnit) {
        if (expiration <= 0) {
            return false;
        }
        this.opsForValue().set(key, value, expiration, timeUnit);
        return true;
    }

    /**
     * 从 {@code key} 指定的 Hash 中获取 {@code field} 的值
     *
     * @param field Hash 字段
     */
    default Object get(K key, Serializable field) {
        return this.opsForHash().get(key, field);
    }

    /**
     * 从 {@code key} 指定的 Hash 中获取 {@code field} 的值,
     * 并根据 {@code clazz} 进行类型转换
     *
     * @param field Hash 字段
     */
    default <T> T get(K key, Object field, @NotNull Class<T> clazz) {
        Object obj = this.opsForHash().get(key, field);
        if (obj == null) {
            return null;
        }
        if (!clazz.isAssignableFrom(obj.getClass())) {  // 类型无法转换
            throw new ClassCastException("The \"" + obj.getClass() + "\" is not equal to \"" + clazz +
                    "\" and does not inherit from \"" + clazz + "\".");
        }
        return (T) obj;
    }

    /**
     * 从 {@code key} 指定的 Hash 中获取所有的字段和值
     */
    default Map<Object, Object> getAll(K key) {
        return this.opsForHash().entries(key);
    }

    /**
     * 将 {@code value} 写入 {@code key} 所指定的 Hash 中的 {@code field}
     *
     * @param field Hash 字段
     */
    default IRedisHelper<K, V> put(K key, Serializable field, Object value) {
        this.opsForHash().put(key, field, value);
        return this;
    }

    /**
     * 将 {@code map} {@code put} 到 {@code key} 指定的 Hash 中
     */
    default IRedisHelper<K, V> put(K key, Map<Serializable, ?> map) {
        this.opsForHash().putAll(key, map);
        return this;
    }

    /**
     * 当 {@code key} 所指定的 Hash 中的 {@code field} 不存在时, 将 {@code value} 写入
     *
     * @param field Hash 字段
     */
    default boolean putIfAbsent(K key, Serializable field, Object value) {
        return this.opsForHash().putIfAbsent(key, field, value);
    }

    /**
     * 从 {@code key} 中获取所有的 {@code fields}
     *
     * @return 当在事物或管道中使用时, 将会返回 {@code null}
     */
    default Set<Object> fields(K key) {
        return this.opsForHash().keys(key);
    }

    /**
     * 设置键的同时为其设置 过期时间点
     *
     * @param expiration 过期时间点
     * @return 当在事物或管道中使用时, 将会返回 {@code null}
     */
    default Boolean expireAt(K key, @NotNull Date expiration) {
        return this.getTemplate().expireAt(key, expiration);
    }

    /**
     * 设置键的同时为其设置 过期时间
     *
     * @param timestamp 过期时间戳 (单位: ms)
     * @return 当在事物或管道中使用时, 将会返回 {@code null}
     */
    default Boolean expire(K key, long timestamp) {
        return this.expire(key, timestamp, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置的 过期时间
     *
     * @param expiration 过期时间
     * @param timeUnit   时间单位
     * @return 当在事物或管道中使用时, 将会返回 {@code null}
     */
    default Boolean expire(K key, long expiration, @NotNull TimeUnit timeUnit) {
        return this.getTemplate().expire(key, expiration, timeUnit);
    }

    /**
     * 获取指定键的过期时间 (Tome to Live, TTL)
     *
     * @return 过期时间 (单位: s). 在管道或事务中使用时, 将会返回 {@code null}.
     * 当 {@code key} 永不过期时, 将返回 {@link IRedisHelper#EXPIRE_PERPETUAL}.
     */
    default Long getExpire(K key) {
        return this.getTemplate().getExpire(key);
    }

    /**
     * 删除指定的 {@code key}
     */
    default boolean delete(K key) {
        return BooleanUtils.toBoolean(this.getTemplate().delete(key));
    }

    /**
     * 删除指定的 {@code keys}
     *
     * @return 成功删除的 {@code key} 的数量.
     * 当在事务或管道中使用时, 返回值为 {@code null}.
     */
    default Long delete(Collection<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0L;
        }
        return this.getTemplate().delete(keys);
    }

    /**
     * 删除指定 {@code key} 中的 {@code field}
     *
     * @return 当在事务或管道中使用时, 返回值为 {@code null}.
     */
    default Boolean delete(K key, Object field) {
        Long count = this.opsForHash().delete(key, field);
        return count == null ? null : count > 0;
    }

    /**
     * 删除指定 {@code key} 中的 {@code fields}
     *
     * @return 成功删除的 {@code field} 的数量.
     * 当在事务或管道中使用时, 返回值为 {@code null}.
     */
    default Long delete(K key, Collection<Object> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return 0L;
        }
        return this.opsForHash().delete(key, fields);
    }

    /**
     * 获取指定 {@code key} 的类型
     */
    default DataType type(K key) {
        return this.getTemplate().type(key);
    }

    /**
     * 对指定 {@code key} 的值执行原子性的递增/递减操作
     * <p>
     * NOTES: 当指定 {@code key} 所关联的 {@code value} 的类型是整数时才可使用
     * </p>
     *
     * @param delta 可以为非正数
     * @return 递增或递减操作之后 {@code key} 所对应的值.
     * 当在事务或管道中使用时, 返回值为 {@code null}.
     */
    default Long increment(K key, long delta) {
        return this.opsForValue().increment(key, delta);
    }

    /**
     * 对指定 {@code key} 的值执行原子性的递增/递减操作
     * <p>
     * NOTES: 当指定 {@code key} 所关联的 {@code value} 的类型是整数时才可使用
     * </p>
     *
     * @param delta 可以为非正数
     * @return 递增或递减操作之后 {@code key} 所对应的值.
     * 当在事务或管道中使用时, 返回值为 {@code null}.
     */
    default Double increment(K key, double delta) {
        return this.opsForValue().increment(key, delta);
    }

    /**
     * 对指定 {@code key} 中的 {@code field} 的值执行原子性的递增/递减操作
     * <p>
     * NOTES: 当指定 {@code key} 所关联的 {@code value} 的类型是整数时才可使用
     * </p>
     *
     * @param delta 可以为非正数
     * @return 递增或递减操作之后 {@code key} 所对应的值.
     * 当在事务或管道中使用时, 返回值为 {@code null}.
     */
    default Long increment(K key, Object field, long delta) {
        return this.opsForHash().increment(key, field, delta);
    }

    /**
     * 对指定 {@code key} 中的 {@code field} 的值执行原子性的递增/递减操作
     * <p>
     * NOTES: 当指定 {@code key} 所关联的 {@code value} 的类型是整数时才可使用
     * </p>
     *
     * @param delta 可以为非正数
     * @return 递增或递减操作之后 {@code key} 所对应的值.
     * 当在事务或管道中使用时, 返回值为 {@code null}.
     */
    default Double increment(K key, Object field, double delta) {
        return this.opsForHash().increment(key, field, delta);
    }

    /**
     * 检查 {@code key} 是否存在, 存在则返回 true; 否则返回 false
     * <p>在事务或管道中将会返回 null</p>
     */
    default boolean hasKey(K key) {
        return BooleanUtils.toBoolean(this.getTemplate().hasKey(key));
    }

    /**
     * 检查 {@code key} 中指定的 {@code field} 是否存在, 存在则返回 true; 否则返回 false
     * <p>在事务或管道中将会返回 null</p>
     */
    default boolean hasKey(K key, Serializable field) {
        return this.opsForHash().hasKey(key, field);
    }
}
