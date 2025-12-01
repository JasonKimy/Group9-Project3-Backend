package com.example.restservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private static final String TABLE_NAME = "users";

    public UserService(RestTemplate restTemplate, ObjectMapper objectMapper) {
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
     * Get all users
     */
    public List<User> findAll() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    getTableUrl(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get user by ID
     */
    public Optional<User> findById(String id) {
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

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get user by username
     */
    public Optional<User> findByUsername(String username) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("username", "eq." + username)
                    .queryParam("limit", "1")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get user by email
     */
    public Optional<User> findByEmail(String email) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("email", "eq." + email)
                    .queryParam("limit", "1")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Create a new user
     */
    public User createUser(String username, String password, String email) {
        User user = new User(
                UUID.randomUUID().toString(),
                username,
                password, // Note: In production, this should be hashed before calling this method
                email,
                Instant.now(),
                Instant.now()
        );

        try {
            String json = objectMapper.writeValueAsString(user);
            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    getTableUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? user : users.get(0);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return user;
        }
    }

    /**
     * Update user
     */
    public Optional<User> updateUser(String id, User updatedUser) {
        try {
            updatedUser.setUpdatedAt(Instant.now());
            String json = objectMapper.writeValueAsString(updatedUser);

            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("id", "eq." + id)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    String.class
            );

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Delete user
     */
    public boolean deleteUser(String id) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("id", "eq." + id)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return findByEmail(email).isPresent();
    }

    /**
     * Update user's favorite challenge 1
     */
    public Optional<User> updateFavChallenge1(String id, String favChallenge1) {
        try {
            Optional<User> userOpt = findById(id);
            if (userOpt.isEmpty()) {
                return Optional.empty();
            }

            User user = userOpt.get();
            user.setFavChallenge1(favChallenge1);
            user.setUpdatedAt(Instant.now());

            String json = objectMapper.writeValueAsString(user);
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("id", "eq." + id)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    String.class
            );

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            System.err.println("Error updating favorite challenge 1: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Update user's favorite challenge 2
     */
    public Optional<User> updateFavChallenge2(String id, String favChallenge2) {
        try {
            Optional<User> userOpt = findById(id);
            if (userOpt.isEmpty()) {
                return Optional.empty();
            }

            User user = userOpt.get();
            user.setFavChallenge2(favChallenge2);
            user.setUpdatedAt(Instant.now());

            String json = objectMapper.writeValueAsString(user);
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("id", "eq." + id)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    String.class
            );

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            System.err.println("Error updating favorite challenge 2: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Update both favorite challenges at once
     */
    public Optional<User> updateFavChallenges(String id, String favChallenge1, String favChallenge2) {
        try {
            Optional<User> userOpt = findById(id);
            if (userOpt.isEmpty()) {
                return Optional.empty();
            }

            User user = userOpt.get();
            user.setFavChallenge1(favChallenge1);
            user.setFavChallenge2(favChallenge2);
            user.setUpdatedAt(Instant.now());

            String json = objectMapper.writeValueAsString(user);
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("id", "eq." + id)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    String.class
            );

            List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            System.err.println("Error updating favorite challenges: " + e.getMessage());
            return Optional.empty();
        }
    }
}
