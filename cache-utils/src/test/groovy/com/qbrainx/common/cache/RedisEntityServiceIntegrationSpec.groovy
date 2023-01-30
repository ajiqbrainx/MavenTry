package com.dijta.common.cache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import spock.lang.Ignore

import java.time.LocalDateTime

@ActiveProfiles("redis")
@Ignore
class RedisEntityServiceIntegrationSpec extends AbstractIntegrationSpec {

    private static final LocalDateTime NOW = LocalDateTime.now()

    @Autowired
    private EntityService service

    def "validate cache"() {
        when: "find cache without insertion"
        def cache = service.findByName("cache")

        then: "check there is no cache present"
        !cache.present

        when: "insert new cache into database"
        def save = service.save(Entity.of("NEW"))

        then: "check generated id from database"
        def id = save.id

        when: "query cache by name"
        def findByName = service.findByName("NEW")

        then: "verify the cache by name"
        findByName.present
        findByName.get().name == "NEW"

        when: "update the info into database"
        def get = findByName.get()
        get.updateTime = NOW
        def update = service.update(get)

        then: "verify updated value"
        update
        update.name == "NEW"
        update.updateTime == NOW

        when: "query data from cache"
        def fromCache = service.findByName("NEW")

        then:
        fromCache.present
        fromCache.get().name == "NEW"
        !fromCache.get().updateTime

        when: "clear cache from memory"
        service.clear()

        then:
        def newCache = service.findByName("NEW")
        newCache.present
        newCache.get().name == "NEW"
        newCache.get().getUpdateTime() == NOW

        when:
        def byId = service.findById(id)

        then:
        byId.present
        with(byId.get()) {
            name == "NEW"
            updateTime == NOW
        }
    }
}