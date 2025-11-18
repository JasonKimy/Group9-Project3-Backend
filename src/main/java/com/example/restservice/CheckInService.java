package com.example.restservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CheckInService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private static final String TABLE_NAME = "checkins";

    public CheckInService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Prefer", "return=representation");
        return headers;
    }

    private String getTableUrl() {
        return supabaseUrl + "/rest/v1/" + TABLE_NAME;
    }

    public CheckIn createCheckIn(String userId, String placeId, String photoUri) {
        CheckIn checkIn = new CheckIn(UUID.randomUUID().toString(), userId, placeId, Instant.now(), photoUri);

        try {
            String json = objectMapper.writeValueAsString(checkIn);
            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    getTableUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            List<CheckIn> checkIns = objectMapper.readValue(response.getBody(), new TypeReference<List<CheckIn>>() {});
            return checkIns.isEmpty() ? checkIn : checkIns.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return checkIn;
        }
    }

    public boolean hasUserCheckedIn(String userId, String placeId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("userId", "eq." + userId)
                    .queryParam("placeId", "eq." + placeId)
                    .queryParam("limit", "1")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<CheckIn> checkIns = objectMapper.readValue(response.getBody(), new TypeReference<List<CheckIn>>() {});
            return !checkIns.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
