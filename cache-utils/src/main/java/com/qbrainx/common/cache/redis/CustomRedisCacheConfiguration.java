package com.qbrainx.common.cache.redis;

import com.qbrainx.common.lang.Validations;
import com.qbrainx.common.message.MessageCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qbrainx.common.cache.CustomCacheProperties;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.qbrainx.common.message.MessageConstants.NON_NULL;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
@ConditionalOnProperty(value = "qbrainx.cache.type", havingValue = "redis")
@Log4j2
public class CustomRedisCacheConfiguration {

    private final CustomCacheProperties customCacheProperties;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public CustomRedisCacheConfiguration(final CustomCacheProperties customCacheProperties) {
        log.info("Loading Redis cache configuration {} ", customCacheProperties);
        this.customCacheProperties = customCacheProperties;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        final String host =
            Validations.notNull(
                customCacheProperties.getRedis().getHost(),
                MessageCode.error(NON_NULL, "property qbrainx.cache.redis.host", "Should Not Null"));

        final Integer port =
            Validations.notNull(
                customCacheProperties.getRedis().getPort(),
                MessageCode.error(NON_NULL, "property qbrainx.cache.redis.port", "Should Not Null"));

        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisSerializer<Object> genericSerializer(final ObjectMapper objectMapper) {
        return new JdkSerializationRedisSerializer();
    }

    @Bean
    @Primary
    public CacheManager cacheManager(
        final RedisConnectionFactory redisConnectionFactory,
        final RedisSerializer<Object> genericSerializer) {

        final Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        final CustomCacheProperties.RedisProperties redis = customCacheProperties.getRedis();

        for (final Map.Entry<String, Long> cacheEntry : redis.getCacheTimeoutInSeconds().entrySet()) {

            final long timeToLiveSeconds =
                Validations.notNull(cacheEntry.getValue(), MessageCode.error(
                    NON_NULL,
                    "property qbrainx.cache.redis.cacheTimeoutInSeconds.".concat(cacheEntry.getKey()), "Should not null"));

            final RedisCacheConfiguration redisCacheConfiguration =
                createCacheConfiguration(timeToLiveSeconds,genericSerializer);

            cacheConfigurations.put(cacheEntry.getKey(), redisCacheConfiguration);
        }

        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(createCacheConfiguration(redis.getTimeoutInSeconds(), genericSerializer))
            .withInitialCacheConfigurations(cacheConfigurations).build();
    }

    private static RedisCacheConfiguration createCacheConfiguration(
        final long timeoutInSeconds,
        final RedisSerializer<Object> genericSerializer) {

        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(timeoutInSeconds))
            .serializeValuesWith(fromSerializer(genericSerializer));
    }

}
