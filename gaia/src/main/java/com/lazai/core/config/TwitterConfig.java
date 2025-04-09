package com.lazai.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import twitter4j.Twitter;

import java.util.Collections;


@Configuration
public class TwitterConfig {

    @Value("${twitter.bearer-token}")
    private String bearerToken;

    @Bean
    public Twitter twitter() {
        return Twitter.getInstance();
    }


    @Bean
    public RestTemplate twitterRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + bearerToken);
            return execution.execute(request, body);
        }));
        return restTemplate;
    }

}
