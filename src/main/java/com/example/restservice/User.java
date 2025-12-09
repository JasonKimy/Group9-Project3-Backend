package com.example.restservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class User {
    private String id;          // UUID
    private String username;
    private String password;
    private String email;
    
    @JsonProperty("created_at")
    private Instant createdAt;
    
    @JsonProperty("updated_at")
    private Instant updatedAt;
    
    @JsonProperty("fav_challenge_1")
    private String favChallenge1;
    
    @JsonProperty("fav_challenge_2")
    private String favChallenge2;
    
    private Long points;
    
    @JsonProperty("avatar_url")
    private String avatarUrl;

    public User() {}

    public User(String id, String username, String password, String email, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.points = 0L;
        this.avatarUrl = "../assets/Wander-Avatars/Normal/normal1.png";
    }

    public User(String id, String username, String password, String email, Instant createdAt, Instant updatedAt, 
                String favChallenge1, String favChallenge2) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.favChallenge1 = favChallenge1;
        this.favChallenge2 = favChallenge2;
        this.points = 0L;
        this.avatarUrl = "../assets/Wander-Avatars/Normal/normal1.png";
    }

    public User(String id, String username, String password, String email, Instant createdAt, Instant updatedAt, 
                String favChallenge1, String favChallenge2, Long points) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.favChallenge1 = favChallenge1;
        this.favChallenge2 = favChallenge2;
        this.points = points;
        this.avatarUrl = "../assets/Wander-Avatars/Normal/normal1.png";
    }

    public User(String id, String username, String password, String email, Instant createdAt, Instant updatedAt, 
                String favChallenge1, String favChallenge2, Long points, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.favChallenge1 = favChallenge1;
        this.favChallenge2 = favChallenge2;
        this.points = points;
        this.avatarUrl = avatarUrl;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getFavChallenge1() { return favChallenge1; }
    public void setFavChallenge1(String favChallenge1) { this.favChallenge1 = favChallenge1; }

    public String getFavChallenge2() { return favChallenge2; }
    public void setFavChallenge2(String favChallenge2) { this.favChallenge2 = favChallenge2; }

    public Long getPoints() { return points; }
    public void setPoints(Long points) { this.points = points; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}

