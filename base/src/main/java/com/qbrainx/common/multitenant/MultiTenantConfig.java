package com.qbrainx.common.multitenant;

import javax.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qbrainx.common.lang.Optionals;
import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageType;
import com.qbrainx.common.security.ISecurityContext;


@Configuration
@EnableConfigurationProperties(MultiTenantConfigProperties.class)
public class MultiTenantConfig {

  @Bean
  public MultiTenantIdFilterAspect multiTenantIdFilterAspect(
      final EntityManager entityManager,
      final ISecurityContext iSecurityContext) {

    return new MultiTenantIdFilterAspect(entityManager, iSecurityContext);
  }

  @Bean
  public MultiTenantBeforeCreateEventListener multiTenantBeforeCreateEventListener(
      final ISecurityContext securityContext) {

    return new MultiTenantBeforeCreateEventListener(securityContext);
  }

  @Bean
  @ConditionalOnProperty(value = "custom.multitenant.type", havingValue = "config")
  public AdditionalTenantFilterProvider configAdditionalTenantFilterProvider(
      final MultiTenantConfigProperties multiTenantConfigProperties) {

    final Long additionalTenantId = Optionals
        .checkPresent(multiTenantConfigProperties.getTenantId(), MessageCode.builder()
            .code("custom.multitenant.tenantId")
            .type(MessageType.ERROR)
            .build());

    return () -> additionalTenantId;
  }
}
