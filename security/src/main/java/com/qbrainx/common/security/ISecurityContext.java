

package com.qbrainx.common.security;

//import org.springframework.security.core.Authentication;

import org.springframework.security.core.Authentication;

        import java.util.Optional;
import java.util.function.Supplier;

public interface ISecurityContext {

    Optional<ISecurityPrincipal> getOptionalPrincipal();

    ISecurityPrincipal getPrincipal();

    Long getTenantId();

    boolean isMultiTenantFilterEnabled();

    boolean isMultiTenantInFilterEnabled();

    void setTenantIds(Long... tenantIds);

    Long[] getTenantIds();

    void disableMultiTenantFilter();

    void setTenantId(Long tenantId);

    Authentication getAuthentication();

    Boolean check(String securityExpression);

    <T> T authorize(String securityExpression, Supplier<T> realFunction);

    void setPrincipal(ISecurityPrincipal securityPrincipal);
}
