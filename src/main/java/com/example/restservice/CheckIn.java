package com.example.restservice;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private User user;

    private String placeId; // ID from Supabase place
    private double latitude;
    private double longitude;
    private String photoUrl;
    private LocalDateTime time = LocalDateTime.now();

    public CheckIn() {}

    public CheckIn(User user, String placeId, double latitude, double longitude, String photoUrl) {
        this.user = user;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrl = photoUrl;
    }

    // getters + setters
}
