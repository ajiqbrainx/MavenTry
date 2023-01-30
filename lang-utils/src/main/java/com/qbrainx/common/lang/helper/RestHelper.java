package com.qbrainx.common.lang.helper;

import com.qbrainx.common.exception.NotFoundException;
import com.qbrainx.common.message.MessageCode;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public final class RestHelper {
    private final RestTemplate restTemplate;

    public RestHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T, U> T callRestApi(final String url, final HttpMethod httpMethod,
                                   final U requestEntity, final Class<T> t, HttpHeaders headers) {
        final ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, returnRequest(requestEntity, headers), t);
        if (response.getBody() == null || !HttpStatus.OK.equals(response.getStatusCode())) {
            throw new NotFoundException(MessageCode.error(
                    response.getStatusCode().toString(), "Unable to derive response from url:" + url));
        }
        return response.getBody();
    }

    private <U> HttpEntity<U> returnRequest(final U requestEntity, HttpHeaders headers) {
        return new HttpEntity<>(requestEntity, headers);
    }
}
