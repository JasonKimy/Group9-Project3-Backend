package com.example.restservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        // Create a custom request factory that enables PATCH method
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                
                // Enable PATCH method by using X-HTTP-Method-Override workaround if needed
                if ("PATCH".equals(httpMethod)) {
                    // Use reflection to allow PATCH method (workaround for Java 17 HttpURLConnection)
                    try {
                        java.lang.reflect.Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
                        methodsField.setAccessible(true);
                        
                        String[] methods = (String[]) methodsField.get(null);
                        java.util.Set<String> methodSet = new java.util.LinkedHashSet<>(java.util.Arrays.asList(methods));
                        methodSet.add("PATCH");
                        methodsField.set(null, methodSet.toArray(new String[0]));
                    } catch (Exception e) {
                        throw new IOException("Failed to enable PATCH method", e);
                    }
                }
            }
        };
        
        return new RestTemplate(requestFactory);
    }
}
