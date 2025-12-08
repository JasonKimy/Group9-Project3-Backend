package com.example.restservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;

@Configuration
public class RestTemplateConfig {
    
    static {
        // Enable PATCH method globally at class load time using reflection
        try {
            java.lang.reflect.Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            methodsField.setAccessible(true);
            
            String[] existingMethods = (String[]) methodsField.get(null);
            java.util.Set<String> methodSet = new java.util.LinkedHashSet<>(java.util.Arrays.asList(existingMethods));
            methodSet.add("PATCH");
            methodsField.set(null, methodSet.toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException("Failed to enable PATCH method", e);
        }
    }
    
    @Bean
    public RestTemplate restTemplate() {
        // Use SimpleClientHttpRequestFactory with PATCH enabled via static block above
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        return new RestTemplate(requestFactory);
    }
}
