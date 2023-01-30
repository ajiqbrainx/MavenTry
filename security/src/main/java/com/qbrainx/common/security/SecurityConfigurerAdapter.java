

package com.qbrainx.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.inject.Inject;

public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final CustomSecurityConfigProperties dijtaSecurityConfigProperties;
    private final ICustomAuthenticationTokenService dijtaAuthenticationTokenService;

    @Inject
    public SecurityConfigurerAdapter(
        final CustomSecurityConfigProperties customSecurityConfigProperties,
        final ICustomAuthenticationTokenService dijtaAuthenticationTokenService) {

        this.dijtaSecurityConfigProperties = customSecurityConfigProperties;
        this.dijtaAuthenticationTokenService = dijtaAuthenticationTokenService;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http.httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .csrf()
            .disable()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .anonymous().disable()
            .addFilterBefore(
                new CustomAuthenticationFilter(dijtaAuthenticationTokenService),
                UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void configure(final WebSecurity web) {
        if (dijtaSecurityConfigProperties.isExcludePathPresent()) {
            web.ignoring().antMatchers(dijtaSecurityConfigProperties.getExcludePathAsArray());
        }
    }

}
