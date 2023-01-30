package com.qbrainx.common.multitenant;

import com.qbrainx.common.identity.FieldAccessUtil;
import com.qbrainx.common.security.ISecurityContext;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
@Log4j2
public class MultiTenantIdFilterAspect {

    private final EntityManager entityManager;
    private final ISecurityContext securityContext;

    private final List<String> methodNamesForTenantId = Arrays.asList("save", "saveall", "saveandflush");

    public MultiTenantIdFilterAspect(final EntityManager entityManager,
                                     final ISecurityContext securityContext) {
        this.entityManager = entityManager;
        this.securityContext = securityContext;
    }

    @Around("execution(* com.qbrainx.common.multitenant.MultiTenantEntityRepository+.*(..))")
    public Object checkAndEnableTenantIdFilter(final ProceedingJoinPoint joinPoint) throws Throwable {

        if (entityManager.isOpen()) {
            final Optional<Session> session = unwrapSession();
            if (session.isPresent()) {
                Session sessionFound = session.get();
                sessionFound.disableFilter("tenantIdFilter");
                sessionFound.disableFilter("tenantIdInFilter");
                if (securityContext.isMultiTenantFilterEnabled()) {
                    final Long tenantId = securityContext.getTenantId();
                    log.debug("Tenant Id filtering added for {} ", tenantId);
                    sessionFound.enableFilter("tenantIdFilter")
                            .setParameter("tenantId", tenantId);
                } else if (securityContext.isMultiTenantInFilterEnabled()) {
                    final Long[] tenantIds = securityContext.getTenantIds();
                    log.debug("Tenant Id in filtering added for {} ", tenantIds.toString());
                    sessionFound.enableFilter("tenantIdInFilter")
                            .setParameterList("tenantIds", tenantIds);
                }
            }
        }
        final String jointPointSignatureName = joinPoint.getSignature().getName().toLowerCase();
        if (methodNamesForTenantId.contains(jointPointSignatureName)) {
            Object obj = joinPoint.getArgs()[0];
            log.debug("--------checkAndEnableTenantIdFilter-----------------------obj:" + obj);
            setTenantIdToNestedObject(obj, securityContext.getTenantId(), new ArrayList<>());
        }
        return joinPoint.proceed();
    }


    private Optional<Session> unwrapSession() {
        try {
            return Optional.ofNullable(entityManager.unwrap(Session.class));
        } catch (final RuntimeException e) {
            log.info("Tenant Id filter will be ignored because transactional session not available!");
            log.debug("Tenant Id filter will be ignored because transactional session not available!", e);
            return Optional.empty();
        }
    }


    @SneakyThrows
    private void setTenantIdToNestedObject(final Object object, final Long tenantId, List<Object> alreadySet) {
        if (object instanceof MultiTenantEntity && !alreadySet.contains(object)) {
            ((MultiTenantEntity) object).setTenantId(tenantId);
            alreadySet.add(object);
            final Field[] allFields = FieldUtils.getAllFields(object.getClass());
            for (Field field : allFields) {
                if (FieldAccessUtil.isAccessible(field)) {
                    field.setAccessible(true);
                    Object o = field.get(object);
                    if (o instanceof Iterable) {
                        for (Object obj : (Iterable) o) {
                            setTenantIdToNestedObject(obj, tenantId, alreadySet);
                        }
                    } else {
                        setTenantIdToNestedObject(o, tenantId, alreadySet);
                    }
                }
            }
        }
        if (object instanceof Iterable) {
            for (final Object obj : (Iterable) object) {
                setTenantIdToNestedObject(obj, tenantId, alreadySet);
            }
        }
    }

}
