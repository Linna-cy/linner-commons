package io.github.linna.cy.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.github.linna.cy.databind.BaseJacksonObjectMapper;
import io.github.linna.cy.helper.SerializableRedisHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;

@Configuration
@AllArgsConstructor
public class BaseRedisConfig extends CachingConfigurerSupport {
    @Getter
    private ObjectMapper objectMapper;

    /**
     * 初始化 {@link BaseRedisConfig}, 并设置默认的 {@link BaseRedisConfig#setObjectMapper(ObjectMapper)}
     * 为 {@link BaseJacksonObjectMapper}
     */
    public BaseRedisConfig() {
        this.objectMapper = new BaseJacksonObjectMapper();
    }

    @Bean
    public RedisSerializer<Object> generateDefaultRedisSerializer() {
        // Json 序列化器
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // Json 映射器 (不要直接使用, 应该拷贝后再设置到 Json 序列化器中)
        this.objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        this.objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        jsonRedisSerializer.setObjectMapper(this.objectMapper);

        return jsonRedisSerializer;
    }

    @Bean
    public RedisTemplate<Serializable, Object> generateDefaultRedisTemplate(RedisConnectionFactory connectionFactory,
                                                                            RedisSerializer<Object> redisSerializer) {
        RedisTemplate<Serializable, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);
        // 默认序列化方式
        redisTemplate.setDefaultSerializer(redisSerializer);

        // Key 序列化方式
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);

        // Value 序列化方式
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public SerializableRedisHelper<Object> generateDefaultRedisHelper(RedisTemplate<Serializable, Object> redisTemplate) {
        return new SerializableRedisHelper<>(redisTemplate);
    }


    public BaseRedisConfig setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        return this;
    }
}
