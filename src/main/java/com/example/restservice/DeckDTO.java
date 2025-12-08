package com.example.restservice;

import java.time.Instant;
import java.util.List;

public class DeckDTO {
    private Long id;
    private Instant createdAt;
    private String userId;
    private String category;
    private List<Place> places;

    public DeckDTO() {}

    public DeckDTO(Long id, Instant createdAt, String userId, String category, List<Place> places) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.category = category;
        this.places = places;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }
}
