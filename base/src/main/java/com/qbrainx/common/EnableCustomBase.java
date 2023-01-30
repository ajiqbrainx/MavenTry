package com.qbrainx.common;

import com.qbrainx.common.auditing.AuditingConfig;
import com.qbrainx.common.cache.EnableCustomCache;
import com.qbrainx.common.exception.ExceptionConfig;
import com.qbrainx.common.identity.IdentityConfig;
import com.qbrainx.common.multitenant.MultiTenantConfig;
import com.qbrainx.common.rest.RestDatabindConfig;
import com.qbrainx.common.security.SecurityConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
    AuditingConfig.class,
    MultiTenantConfig.class,
    SecurityConfiguration.class,
    IdentityConfig.class,
    ExceptionConfig.class,
    RestDatabindConfig.class
})
@EnableCustomCache
public @interface EnableCustomBase {
}
