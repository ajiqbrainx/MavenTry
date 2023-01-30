package com.qbrainx.common.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("dijta.cache")
@Getter
@Setter
public class CustomCacheProperties {

    /**
     * Which type of cache need to be used. default it will use local(Caffeine)
     */
    private CacheType type = CacheType.EMBEDDED;

    /**
     * Optional properties for redis cache if the type of redis
     */
    private RedisProperties redis = new RedisProperties();

    /**
     * Optional properties for embedded cache if the type is embedded.
     */
    private EmbeddedProperties embedded = new EmbeddedProperties();

    public enum CacheType {
        REDIS, EMBEDDED
    }

    @Getter
    @Setter
    public static class RedisProperties {

        private String host = "localhost";
        private int port = 6379;
        private long timeoutInSeconds = 600;
        private Map<String, Long> cacheTimeoutInSeconds = new HashMap<>();

    }

    @Getter
    @Setter
    public static class EmbeddedProperties {
        private long maximumSize = 10_000;
        private long expireAfterWrite = 10;//minutes
    }
}