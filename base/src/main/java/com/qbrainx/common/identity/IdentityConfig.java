package com.qbrainx.common.identity;

import org.dozer.DozerBeanMapper;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
public class IdentityConfig {

    @Bean
    public Integrator integrator() {
        return new EventListenerIntegrator();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(final Integrator integrator) {
        return hibernateProperties ->
            hibernateProperties.put(
                "hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(integrator));
    }

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        final DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.setMappingFiles(new ArrayList<>(Collections.singleton("dozerJdk8Converters.xml")));
        return dozerBeanMapper;
    }

}
