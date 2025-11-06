package com.example.restservice;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String theme;

    @ElementCollection
    private List<String> placeIds; // Links to places in Supabase

    public Deck() {}

    public Deck(String name, String theme, List<String> placeIds) {
        this.name = name;
        this.theme = theme;
        this.placeIds = placeIds;
    }

    // getters + setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getTheme() { return theme; }
    public List<String> getPlaceIds() { return placeIds; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setTheme(String theme) { this.theme = theme; }
    public void setPlaceIds(List<String> placeIds) { this.placeIds = placeIds; }
}
