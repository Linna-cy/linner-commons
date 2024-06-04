package io.github.linna.cy.helper;

import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.HashMapper;

public interface RedisOperationsHelper<K, V> extends RedisBaseHelper<K, V> {
    /**
     * @see RedisTemplate#opsForValue()
     */
    default ValueOperations<K, V> opsForValue() {
        return this.getTemplate().opsForValue();
    }

    /**
     * @see RedisTemplate#opsForHash()
     */
    default <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return this.getTemplate().opsForHash();
    }

    /**
     * @see RedisTemplate#opsForSet()
     */
    default SetOperations<K, V> opsForSet() {
        return this.getTemplate().opsForSet();
    }

    /**
     * @see RedisTemplate#opsForList()
     */
    default ListOperations<K, V> opsForList() {
        return this.getTemplate().opsForList();
    }

    /**
     * @see RedisTemplate#opsForZSet()
     */
    default ZSetOperations<K, V> opsForZSet() {
        return this.getTemplate().opsForZSet();
    }

    /**
     * @see RedisTemplate#opsForCluster()
     */
    default ClusterOperations<K, V> opsForCluster() {
        return this.getTemplate().opsForCluster();
    }

    /**
     * @see RedisTemplate#opsForGeo()
     */
    default GeoOperations<K, V> opsForGeo() {
        return this.getTemplate().opsForGeo();
    }

    /**
     * @see RedisTemplate#opsForHyperLogLog()
     */
    default HyperLogLogOperations<K, V> opsForHyperLogLog() {
        return this.getTemplate().opsForHyperLogLog();
    }

    /**
     * @see RedisTemplate#opsForStream()
     */
    default <HK, HV> StreamOperations<K, HK, HV> opsForStream() {
        return this.getTemplate().opsForStream();
    }

    /**
     * @see RedisTemplate#opsForStream(HashMapper)
     */
    default <HK, HV> StreamOperations<K, HK, HV> opsForStream(HashMapper<? super K, ? super HK, ? super HV> hashMapper) {
        return this.getTemplate().opsForStream();
    }


    /**
     * @see RedisTemplate#boundGeoOps(K)
     */
    default BoundGeoOperations<K, V> boundGeoOps(K key) {
        return this.getTemplate().boundGeoOps(key);
    }

    /**
     * @see RedisTemplate#boundHashOps(K)
     */
    default <HK, HV> BoundHashOperations<K, HK, HV> boundHashOps(K key) {
        return this.getTemplate().boundHashOps(key);
    }

    /**
     * @see RedisTemplate#boundListOps(K)
     */
    default BoundListOperations<K, V> boundListOps(K key) {
        return this.getTemplate().boundListOps(key);
    }

    /**
     * @see RedisTemplate#boundSetOps(K)
     */
    default BoundSetOperations<K, V> boundSetOps(K key) {
        return this.getTemplate().boundSetOps(key);
    }

    /**
     * @see RedisTemplate#boundStreamOps(K)
     */
    default <HK, HV> BoundStreamOperations<K, HK, HV> boundStreamOps(K key) {
        return this.getTemplate().boundStreamOps(key);
    }

    /**
     * @see RedisTemplate#boundValueOps(K)
     */
    default BoundValueOperations<K, V> boundValueOps(K key) {
        return this.getTemplate().boundValueOps(key);
    }

    /**
     * @see RedisTemplate#boundZSetOps(K)
     */
    default BoundZSetOperations<K, V> boundZSetOps(K key) {
        return this.getTemplate().boundZSetOps(key);
    }
}
