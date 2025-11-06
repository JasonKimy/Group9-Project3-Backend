package com.example.restservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SupabaseService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.key}")
    private String supabaseKey;
    
    private static final String TABLE_NAME = "places";
    
    public SupabaseService(RestTemplate restTemplate, ObjectMapper objectMapper) {
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
    
    /**
     * Get all places
     */
    public List<Place> findAll() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                getTableUrl(),
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching all places: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get place by ID
     */
    public Optional<Place> findById(String id) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("id", "eq." + id)
                .queryParam("limit", "1")
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            List<Place> places = objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
            return places.isEmpty() ? Optional.empty() : Optional.of(places.get(0));
        } catch (Exception e) {
            System.err.println("Error fetching place by ID: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Find places by category
     */
    public List<Place> findByCategory(String category) {
        return findByField("category", category);
    }
    
    /**
     * Find places by city
     */
    public List<Place> findByCity(String city) {
        return findByField("city", city);
    }
    
    /**
     * Find places by subcategory
     */
    public List<Place> findBySubcategory(String subcategory) {
        return findByField("subcategory", subcategory);
    }
    
    /**
     * Find places by category and city
     */
    public List<Place> findByCategoryAndCity(String category, String city) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("category", "eq." + category)
                .queryParam("city", "eq." + city)
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching places by category and city: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Search places by name (case insensitive)
     */
    public List<Place> findByNameContaining(String name) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("name", "ilike.*" + name + "*")
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
        } catch (Exception e) {
            System.err.println("Error searching places by name: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Search places by description keyword
     */
    public List<Place> searchByDescription(String keyword) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("description", "ilike.*" + keyword + "*")
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
        } catch (Exception e) {
            System.err.println("Error searching places by description: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get places within bounding box
     */
    public List<Place> findPlacesInBoundingBox(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("lat", "gte." + minLat)
                .queryParam("lat", "lte." + maxLat)
                .queryParam("lon", "gte." + minLon)
                .queryParam("lon", "lte." + maxLon)
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching places in bounding box: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all unique categories
     */
    public List<String> findAllCategories() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("select", "category")
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            List<Place> places = objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
            return places.stream()
                .map(Place::getCategory)
                .distinct()
                .sorted()
                .toList();
        } catch (Exception e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all unique cities
     */
    public List<String> findAllCities() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("select", "city")
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            List<Place> places = objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
            return places.stream()
                .map(Place::getCity)
                .distinct()
                .sorted()
                .toList();
        } catch (Exception e) {
            System.err.println("Error fetching cities: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get count of places
     */
    public long count() {
        try {
            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "count=exact");
            
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam("select", "id")
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.HEAD,
                entity,
                String.class
            );
            
            String contentRange = response.getHeaders().getFirst("Content-Range");
            if (contentRange != null && contentRange.contains("/")) {
                return Long.parseLong(contentRange.split("/")[1]);
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error counting places: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Save a single place (insert or update)
     */
    public Place save(Place place) {
        try {
            String json = objectMapper.writeValueAsString(place);
            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(
                getTableUrl(),
                HttpMethod.POST,
                entity,
                String.class
            );
            
            List<Place> places = objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
            return places.isEmpty() ? place : places.get(0);
        } catch (Exception e) {
            System.err.println("Error saving place: " + e.getMessage());
            e.printStackTrace();
            return place;
        }
    }
    
    /**
     * Save multiple places (batch insert)
     */
    public List<Place> saveAll(List<Place> places) {
        try {
            String json = objectMapper.writeValueAsString(places);
            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(
                getTableUrl(),
                HttpMethod.POST,
                entity,
                String.class
            );
            
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
        } catch (Exception e) {
            System.err.println("Error saving places: " + e.getMessage());
            e.printStackTrace();
            return places;
        }
    }
    
    /**
     * Helper method to find by any field
     */
    private List<Place> findByField(String fieldName, String value) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                .queryParam(fieldName, "eq." + value)
                .toUriString();
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Place>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching places by " + fieldName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
