package com.qbrainx.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.Set;
import io.vavr.jackson.datatype.VavrModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SecurityPrincipal implements ISecurityPrincipal {

    private static final long serialVersionUID = -2114271096812978182L;

    private static final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
            .modules(new VavrModule())
            .build();

    private static final String ROLE_PREFIX = "ROLE_";

    private Long id;
    private String name;
    private Long tenantId;
    private Set<String> privileges;
    @JsonIgnore
    private BearerToken bearerToken;

    public <T> List<T> getAuthority(Function<String, T> mapper) {
        return getPrivileges()
                .map(mapper)
                .toJavaList();
    }

    public static SecurityPrincipal fromJwt(String response) {
        try {
            Jwt decoded = JwtHelper.decode(response);
            return objectMapper.readValue(decoded.getClaims(), SecurityPrincipal.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Invalid jwt", response), e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Invalid jwt", response), e);
        }
    }

    public String toJwt() {
        try {
            String json = objectMapper.writeValueAsString(this);
            return JwtHelper.encode(json, new MacSigner("DIJTA")).getEncoded();
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Invalid security principal", this), e);
        }
    }
}
