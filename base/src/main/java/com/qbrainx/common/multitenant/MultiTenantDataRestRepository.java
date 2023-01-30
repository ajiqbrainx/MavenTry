package com.qbrainx.common.multitenant;

import org.springframework.data.jpa.repository.JpaRepository;

//Marker interface for filtering logic to be enabled
public interface MultiTenantDataRestRepository<T extends MultiTenantEntity> extends
    JpaRepository<T, Long> {

}
