package com.qbrainx.common.cache;

import com.qbrainx.common.cache.local.EmbeddedCacheConfiguration;
import com.qbrainx.common.cache.redis.CustomRedisCacheConfiguration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CustomCacheProperties.class)
@Import({EmbeddedCacheConfiguration.class, CustomRedisCacheConfiguration.class})
public class CustomCacheConfiguration {
}
