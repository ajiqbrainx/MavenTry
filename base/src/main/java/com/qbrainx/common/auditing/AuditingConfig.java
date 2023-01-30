package com.qbrainx.common.auditing;

import com.qbrainx.common.identity.IdentityConfig;
import com.qbrainx.common.security.ISecurityContext;
import com.qbrainx.common.security.ISecurityPrincipal;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.sun.org.apache.xerces.internal.parsers.SecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@Import(value = {SecurityConfiguration.class, IdentityConfig.class})
public class AuditingConfig {


    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
    }

    @Bean
    public AuditorAware<Long> auditorProvider(final ISecurityContext securityContext) {
        return () -> securityContext.getOptionalPrincipal().map(ISecurityPrincipal::getId);
    }

}
