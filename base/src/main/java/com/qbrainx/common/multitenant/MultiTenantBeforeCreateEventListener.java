package com.qbrainx.common.multitenant;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.event.BeforeCreateEvent;

import com.qbrainx.common.security.ISecurityContext;

@AllArgsConstructor
@Log4j2
public class MultiTenantBeforeCreateEventListener {

  private final ISecurityContext securityContext;

  @EventListener(BeforeCreateEvent.class)
  public void setTenantId(BeforeCreateEvent beforeCreateEvent) {

    Object source = beforeCreateEvent.getSource();

    if (source instanceof MultiTenantEntity) {

      log.info("multi tenant entity create event listener fired ");

      final Long tenantId = securityContext.getTenantId();

      ((MultiTenantEntity) source).setTenantId(tenantId);
    }
  }
}
