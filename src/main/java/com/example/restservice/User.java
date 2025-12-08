package com.example.restservice;

import java.time.Instant;

public class User {
    private String id;          // UUID
    private String username;
    private String password;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    private Long points;

    public User() {}

    public User(String id, String username, String password, String email, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.points = 0L;
    }

    public User(String id, String username, String password, String email, Instant createdAt, Instant updatedAt, Long points) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.points = points;
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

    public Long getPoints() { return points; }
    public void setPoints(Long points) { this.points = points; }
}
