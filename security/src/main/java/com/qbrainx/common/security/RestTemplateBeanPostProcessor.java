

package com.qbrainx.common.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class RestTemplateBeanPostProcessor implements BeanPostProcessor {

    private final ClientHttpRequestInterceptor clientHttpRequestInterceptor;

    public RestTemplateBeanPostProcessor(ISecurityContext securityContext) {
        this.clientHttpRequestInterceptor = (request, body, execution) -> {
            SecurityPrincipal principal = (SecurityPrincipal) securityContext.getPrincipal();
            BearerToken authTokens = principal.getBearerToken();
            HttpHeaders headers = request.getHeaders();
            headers.addAll(authTokens.buildPropagationHeaders());
            return execution.execute(request, body);
        };
    }

    @Override
    public Object postProcessBeforeInitialization(
        Object bean,
        String beanName) {

        ClientHttpRequestInterceptor interceptor = this.clientHttpRequestInterceptor;
        if (bean instanceof RestTemplate) {
            RestTemplate restTemplate = (RestTemplate) bean;
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
            interceptors.add(interceptor);
            restTemplate.setInterceptors(interceptors);
            return bean;
        }
        if (bean instanceof RestTemplateBuilder) {
            RestTemplateBuilder restTemplateBuilder = (RestTemplateBuilder) bean;
            return restTemplateBuilder.additionalInterceptors(interceptor);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
