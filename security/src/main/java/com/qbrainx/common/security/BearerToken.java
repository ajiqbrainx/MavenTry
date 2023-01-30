package com.qbrainx.common.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;
import java.util.function.Function;

@Getter
@Builder
public class BearerToken implements Serializable {

    private static final long serialVersionUID = -8624026746694803045L;

    private final String bearerToken;

    private static boolean isTokenNotEmpty(String token) {
        return token != null && !token.isEmpty();
    }

    public HttpHeaders buildPropagationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        addToHeader(HttpHeaders.AUTHORIZATION, getBearerToken(), headers);
        return headers;
    }

    private void addToHeader(String key, String value, HttpHeaders headers) {
        if (isTokenNotEmpty(value)) {
            headers.add(key, value);
        }
    }

    public static BearerToken buildAuthTokenFromHeader(Function<String, String> headersFunc) {
        return BearerToken.builder()
            .bearerToken(headersFunc.apply(HttpHeaders.AUTHORIZATION))
            .build();
    }

}
