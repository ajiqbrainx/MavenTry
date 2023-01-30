package com.qbrainx.common.identity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@SuppressWarnings("InterfaceNeverImplemented")
public interface BaseEntityRepository<T extends BaseEntity>
    extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
