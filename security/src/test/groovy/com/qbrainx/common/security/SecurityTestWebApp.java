package com.qbrainx.common.security;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebInputException;

import com.qbrainx.common.security.ISecurityContext;
import com.qbrainx.common.security.ISecurityPrincipal;
import com.qbrainx.common.security.SecurityConfiguration;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
@Configuration
@Import(SecurityConfiguration.class)
public class SecurityTestWebApp {

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @RestController
    public static class TestController {

        private ISecurityContext securityContext;
        private RestOperations restTemplate;

        public TestController(ISecurityContext securityContext,
                              RestOperations restOperations) {
            this.securityContext = securityContext;
            this.restTemplate = restOperations;
        }

        @GetMapping("/testauth")
        @PreAuthorize("hasAuthority('SECURED')")
        public String test() {

            return "Test";
        }

        @GetMapping("/cansee")
        public String cansee() {

            return securityContext.authorize("hasAuthority('CAN_SEE')", () -> "Yes Can see");
        }


        public static class DummyObject {
            private String val;

            public DummyObject(String val) {
                this.val = val;
            }

            public String getVal() {
                return val;
            }
        }

        @GetMapping("/opendata")
        public String unAuthorized() {
            return "Open Data";
        }

        @GetMapping("/notloggedin")
        public String notloggedin() {
            return securityContext.getPrincipal().getName();
        }

        @GetMapping("/user")
        public ISecurityPrincipal user() {
            return securityContext.getPrincipal();
        }

        @GetMapping("/propagate")
        public String propagateCall() {
            return restTemplate.getForObject("http://localhost:1234/services/v1/users", String.class);
        }

        @GetMapping("/error")
        public String error() {
            throw new RuntimeException("Some Business Error");
        }

        @GetMapping("/badrequesterror")
        public String badError() {
            throw new ServerWebInputException("bad request");
        }
    }

}
