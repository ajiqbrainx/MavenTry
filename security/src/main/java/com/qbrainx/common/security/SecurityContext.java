

package com.qbrainx.common.security;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
class SecurityContext implements ISecurityContext {

    private final SecurityExpressionChecker securityExpressionChecker;
    private final String adminRoleSecurityExpression;

    SecurityContext(SecurityExpressionChecker securityExpressionChecker, String adminRoleSecurityExpression) {
        this.securityExpressionChecker = securityExpressionChecker;
        this.adminRoleSecurityExpression = adminRoleSecurityExpression;
    }

    @Override
    public Optional<ISecurityPrincipal> getOptionalPrincipal() {
        return getOptionalAuthentication().map(Authentication::getPrincipal).map(ISecurityPrincipal.class::cast);
    }

    @Override
    public ISecurityPrincipal getPrincipal() {
        return (ISecurityPrincipal) getAuthentication().getPrincipal();
    }

    @Override
    public Long getTenantId() {
        return Optional.ofNullable((Long) RequestContextHolder.currentRequestAttributes().getAttribute("overRiddenTenantId", RequestAttributes.SCOPE_REQUEST))
                .map(Optional::of)
                .orElseGet(() -> getOptionalPrincipal().map(ISecurityPrincipal::getTenantId))
                .orElseThrow(() -> new RuntimeException("tenant id not found to filter"));
    }

    @Override
    public boolean isMultiTenantFilterEnabled() {
        return Optional.ofNullable((Boolean) RequestContextHolder.currentRequestAttributes().getAttribute("multiTenantFilterEnabled", RequestAttributes.SCOPE_REQUEST))
                .orElse(true);
    }

    @Override
    public boolean isMultiTenantInFilterEnabled() {
        return Optional.ofNullable((Boolean) RequestContextHolder.currentRequestAttributes().getAttribute("multiTenantInFilterEnabled", RequestAttributes.SCOPE_REQUEST))
                .orElse(false);
    }

    @Override
    public void setTenantIds(final Long... tenantIds) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        requestAttributes.setAttribute("multiTenantFilterEnabled", false, RequestAttributes.SCOPE_REQUEST);
        requestAttributes.setAttribute("multiTenantInFilterEnabled", true, RequestAttributes.SCOPE_REQUEST);
        requestAttributes.setAttribute("overRiddenTenantIdsList", tenantIds, RequestAttributes.SCOPE_REQUEST);
    }

    @Override
    public Long[] getTenantIds() {
        return Optional.ofNullable((Long[]) RequestContextHolder.currentRequestAttributes().getAttribute("overRiddenTenantIdsList", RequestAttributes.SCOPE_REQUEST))
                .orElseThrow(() -> new RuntimeException("no tenant id list specified"));
    }

    @Override
    public void disableMultiTenantFilter() {
        if (check(adminRoleSecurityExpression)) {
            RequestContextHolder.currentRequestAttributes().setAttribute("multiTenantFilterEnabled", false, RequestAttributes.SCOPE_REQUEST);
            RequestContextHolder.currentRequestAttributes().setAttribute("multiTenantInFilterEnabled", false, RequestAttributes.SCOPE_REQUEST);
        }
    }

    @Override
    public void setTenantId(Long tenantId) {
        if (check(adminRoleSecurityExpression)) {
            RequestContextHolder.currentRequestAttributes().setAttribute("overRiddenTenantId", tenantId, RequestAttributes.SCOPE_REQUEST);
        }
    }

    @Override
    public Authentication getAuthentication() {
        return getOptionalAuthentication()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication not found"));
    }

    private Optional<Authentication> getOptionalAuthentication() {
        return Optional.ofNullable(SecurityContextHolder
                .getContext())
                .map(org.springframework.security.core.context.SecurityContext::getAuthentication);
    }

    @Override
    public Boolean check(String securityExpression) {
        return securityExpressionChecker.check(securityExpression, getAuthentication());
    }


    @Override
    public <T> T authorize(String securityExpression, Supplier<T> realFunction) {
        if (check(securityExpression)) {
            return realFunction.get();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Don't have access rights");
        }
    }

    /**
     * Will set the iSecurityPrincipal passed tto the Current Security Context
     *
     * @param iSecurityPrincipal : iSecurityPrincipal
     */
    @Override
    public void setPrincipal(final ISecurityPrincipal iSecurityPrincipal) {

        final SecurityPrincipal securityPrincipal = (SecurityPrincipal) iSecurityPrincipal;

        final PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(
                securityPrincipal,
                securityPrincipal.getBearerToken(),
                securityPrincipal.getAuthority(SimpleGrantedAuthority::new));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
