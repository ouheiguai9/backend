package com.byakuya.boot.backend.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.security.jackson2.SecurityJackson2Modules;

import java.io.IOException;

/**
 * created by 田伯光 on 2023/12/08
 */
@Configuration
class RedisConfiguration {
    private static GenericJackson2JsonRedisSerializer commonConfig(GenericJackson2JsonRedisSerializer json) {
        json.configure(objectMapper -> {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
            objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
            objectMapper.registerModules(new JavaTimeModule());
        });
        return json;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        GenericJackson2JsonRedisSerializer json = commonConfig(new GenericJackson2JsonRedisSerializer());
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setValueSerializer(json);
        template.setHashValueSerializer(json);
        template.setDefaultSerializer(json);
        return template;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisTemplate<String, Object> redisTemplate) {
        return (builder) -> {
            RedisCacheConfiguration config = builder.cacheDefaults().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
            builder.cacheDefaults(config);
        };
    }

    @Bean
    RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return commonConfig(new SessionJsonSerializer());
    }


    private static class SessionJsonSerializer extends GenericJackson2JsonRedisSerializer {
        private static final SimpleType VOID_TYPE = SimpleType.constructUnsafe(Void.class);

        SessionJsonSerializer() {
            configure(mapper -> {
                mapper.registerModules(SecurityJackson2Modules.getModules(this.getClass().getClassLoader()));
                mapper.addHandler(new DeserializationProblemHandler() {
                    @Override
                    public JavaType handleUnknownTypeId(DeserializationContext ctxt, JavaType baseType, String subTypeId, TypeIdResolver idResolver, String failureMsg) throws IOException {
                        return VOID_TYPE;
                    }

                    @Override
                    public JavaType handleMissingTypeId(DeserializationContext ctxt, JavaType baseType, TypeIdResolver idResolver, String failureMsg) throws IOException {
                        return VOID_TYPE;
                    }
                });
            });
        }

        @NonNull
        @Override
        protected JavaType resolveType(@NonNull byte[] source, @NonNull Class<?> type) throws IOException {
            try {
                return super.resolveType(source, type);
            } catch (Exception e) {
                return VOID_TYPE;
            }
        }
    }
}
