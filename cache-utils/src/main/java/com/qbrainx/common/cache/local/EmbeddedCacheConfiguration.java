package com.qbrainx.common.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.qbrainx.common.cache.CustomCacheProperties;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(value = "dijta.cache.type", havingValue = "embedded", matchIfMissing = true)
@Log4j2
public class EmbeddedCacheConfiguration {

    private final CustomCacheProperties customCacheProperties;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public EmbeddedCacheConfiguration(final CustomCacheProperties customCacheProperties) {
        log.info("Loading embedded cache with configuration {} ", customCacheProperties);
        this.customCacheProperties = customCacheProperties;
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .maximumSize(customCacheProperties.getEmbedded().getMaximumSize())
            .expireAfterWrite(Duration.ofMinutes(customCacheProperties.getEmbedded().getExpireAfterWrite()));

        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
