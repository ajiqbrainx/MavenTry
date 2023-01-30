package com.dijta.common.cache

import groovy.util.logging.Slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@ConditionalOnProperty(value = "dijta.cache.type", havingValue = "redis")
@Configuration
@Slf4j
class TestRedisConfiguration {

    private final RedisServer redisServer

    TestRedisConfiguration(final DijtaCacheProperties cacheProperties) {
        this.redisServer = new RedisServer(cacheProperties.getRedis().getPort())
    }

    @PostConstruct
    void postConstruct() {
        log.debug("Embedded redis server started >>>>> {} ", redisServer.ports())
        redisServer.start()
    }

    @PreDestroy
    void preDestroy() {
        redisServer.stop()
    }
}
