package com.lazai.utils;


import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpTraceUtils {

    public static String get(String url) throws URISyntaxException {

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> headers = new HttpHeaders();

        headers.add("traceId", MDC.get("traceId"));

        URI uri = new URI(url);

        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);

        ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);

        if (exchange.getStatusCode().equals(HttpStatus.OK)) {

        }

        return exchange.getBody();

    }

}
