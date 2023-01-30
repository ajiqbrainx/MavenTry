

package com.qbrainx.common.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
public class CustomAuthenticationTokenService implements ICustomAuthenticationTokenService {

    private final CustomSecurityConfigProperties customSecurityConfigProperties;
    private final SecurityExpressionChecker securityExpressionChecker;

    public CustomAuthenticationTokenService(
        CustomSecurityConfigProperties customSecurityConfigProperties,
        SecurityExpressionChecker securityExpressionChecker) {

        this.customSecurityConfigProperties = customSecurityConfigProperties;
        this.securityExpressionChecker = securityExpressionChecker;
    }

    @Override
    public Authentication getAuthentication(BearerToken authTokens) {
        try {

            if (StringUtils.isEmpty(authTokens.getBearerToken()) || !authTokens.getBearerToken()
                .startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bearer token not present or invalid");
            }
            String bearerToken = authTokens.getBearerToken().substring(7);
            SecurityPrincipal principal = SecurityPrincipal.fromJwt(bearerToken)
                .toBuilder()
                .bearerToken(authTokens)
                .build();

            PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(
                principal,
                principal.getBearerToken(),
                principal.getAuthority(SimpleGrantedAuthority::new));

            String entryExpression = customSecurityConfigProperties.getEntrySecurityExpression();

            if (!StringUtils.isEmpty(entryExpression)
                && !securityExpressionChecker.check(entryExpression, authenticationToken)) {
                throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    String.format("Don't have enough rights. %s", entryExpression));
            }

            return authenticationToken;
        } catch (ResponseStatusException re) {
            throw re;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                String.format("Unknown error while processing token %s", e.getMessage()));
        }
    }

}
