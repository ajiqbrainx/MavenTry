

package com.qbrainx.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFilter implements Filter {

    private final ICustomAuthenticationTokenService customAuthenticationTokenService;

    public CustomAuthenticationFilter(ICustomAuthenticationTokenService customAuthenticationTokenService) {
        this.customAuthenticationTokenService = customAuthenticationTokenService;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            BearerToken authTokens = BearerToken.buildAuthTokenFromHeader(httpRequest::getHeader);
            Authentication authenticationToken = customAuthenticationTokenService.getAuthentication(authTokens);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (ResponseStatusException re) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(re.getStatus().value(), re.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}
