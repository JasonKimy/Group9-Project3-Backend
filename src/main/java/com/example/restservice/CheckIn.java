package com.example.restservice;

import java.time.Instant;

public class CheckIn {
    private String id;          // UUID
    private String userId;
    private String placeId;
    private Instant timestamp;
    private String photoUri;

    public CheckIn() {}

    public CheckIn(String id, String userId, String placeId, Instant timestamp, String photoUri) {
        this.id = id;
        this.userId = userId;
        this.placeId = placeId;
        this.timestamp = timestamp;
        this.photoUri = photoUri;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
}
