package com.qbrainx.common.cache;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface EntityRepository extends JpaRepository<Entity, Long> {

    Optional<Entity> findByName(String name);
}