package com.dijta.common.cache

import groovy.util.logging.Slf4j
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MSSQLServerContainer
import spock.lang.Specification

@ContextConfiguration(initializers = [Initializer])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [CacheApplication.class])
@Slf4j
class AbstractIntegrationSpec extends Specification {

    private static JdbcDatabaseContainer SQL_SERVER = null

    private static final boolean TEST_CONTAINERS = System.getProperty("testContainers", "false") == "true"

    def setupSpec() {
        if (TEST_CONTAINERS && !SQL_SERVER) {
            SQL_SERVER = new MSSQLServerContainer()
            SQL_SERVER.start()
        }
    }

    @SuppressWarnings('NewGroovyClassNamingConvention')
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            if (TEST_CONTAINERS) {
                log.info("Using database container {}:{}@{}", SQL_SERVER.username, SQL_SERVER.password, SQL_SERVER.jdbcUrl)
                TestPropertyValues.of(
                    "spring.datasource.url=" + SQL_SERVER.jdbcUrl,
                    "spring.datasource.username=" + SQL_SERVER.username,
                    "spring.datasource.password=" + SQL_SERVER.password)
                    .applyTo(configurableApplicationContext.environment)
            }
        }
    }
}