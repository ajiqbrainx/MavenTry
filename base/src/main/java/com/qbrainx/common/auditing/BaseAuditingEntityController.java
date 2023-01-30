package com.qbrainx.common.auditing;

import com.qbrainx.common.identity.BaseEntityController;
import com.qbrainx.common.security.ISecurityContext;

import org.springframework.http.HttpMethod;

import java.util.Map;

public abstract class BaseAuditingEntityController<V extends BaseAuditingDTO, T extends BaseAuditingEntity>
    extends BaseEntityController<V, T> {

    protected BaseAuditingEntityController(final BaseAuditingEntityService<V, T> service,
                                           final ISecurityContext securityContext,
                                           final Map<HttpMethod, String> securityExpression) {
        super(service, securityContext, securityExpression);
    }

}
