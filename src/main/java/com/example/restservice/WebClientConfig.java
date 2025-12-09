package com.example.restservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // WebClient supports PATCH natively without HttpComponents
        return builder.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        // Provide RestTemplate for other services that still use it
        return new RestTemplate();
    }
}
