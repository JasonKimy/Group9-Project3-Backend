package com.example.restservice;

import java.util.List;

public class Deck {
    private String id;          // Usually same as category name
    private String name;
    private String description;
    private String category;
    private List<Place> places;
    private int completedCount;

    public Deck() {}

    public Deck(String id, String name, String description, String category, List<Place> places, int completedCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.places = places;
        this.completedCount = completedCount;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
}
