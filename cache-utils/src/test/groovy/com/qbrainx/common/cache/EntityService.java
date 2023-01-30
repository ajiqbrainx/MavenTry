package com.qbrainx.common.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qbrainx.common.cache.Clearable;

import java.util.Optional;

@Service
@Transactional
class EntityService implements Clearable {

    private final EntityRepository entityRepository;

    EntityService(final EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Cacheable(cacheNames = "cacheService")
    public Optional<Entity> findByName(final String name) {
        return entityRepository.findByName(name);
    }

    public Entity save(final Entity entity) {
        return entityRepository.save(entity);
    }

    public Entity update(final Entity entity) {
        return entityRepository.save(entity);
    }

    public Optional<Entity> findById(final Long id) {
        return entityRepository.findById(id);
    }

    @CacheEvict(cacheNames = "cacheService", allEntries = true)
    @Override
    public void clear() {

    }
}