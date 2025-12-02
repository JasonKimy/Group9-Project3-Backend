package com.example.restservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Deck {
    private Long id;
    
    @JsonProperty("created_at")
    private Instant createdAt;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("place_1-id")
    private String place1Id;
    
    @JsonProperty("place_2-id")
    private String place2Id;
    
    @JsonProperty("place_3-id")
    private String place3Id;
    
    private String category;

    public Deck() {}

    public Deck(String userId, String place1Id, String place2Id, String place3Id, String category) {
        this.userId = userId;
        this.place1Id = place1Id;
        this.place2Id = place2Id;
        this.place3Id = place3Id;
        this.category = category;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPlace1Id() { return place1Id; }
    public void setPlace1Id(String place1Id) { this.place1Id = place1Id; }

    public String getPlace2Id() { return place2Id; }
    public void setPlace2Id(String place2Id) { this.place2Id = place2Id; }

    public String getPlace3Id() { return place3Id; }
    public void setPlace3Id(String place3Id) { this.place3Id = place3Id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
