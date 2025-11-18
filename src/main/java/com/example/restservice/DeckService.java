package com.example.restservice;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeckService {

    private final SupabaseService supabaseService;
    private final CheckInService checkInService;

    public DeckService(SupabaseService supabaseService, CheckInService checkInService) {
        this.supabaseService = supabaseService;
        this.checkInService = checkInService;
    }

    public Deck getDeckForUser(String category, String userId) {
        List<Place> places = supabaseService.findByCategory(category);

        int completedCount = (int) places.stream()
                .filter(place -> checkInService.hasUserCheckedIn(userId, place.getId()))
                .count();

        return new Deck(
                category,
                category.replace("_", " "), // name
                "Explore " + places.size() + " amazing " + category.replace("_", " ").toLowerCase() + " locations",
                category,
                places,
                completedCount
        );
    }

    public List<String> getAllCategories() {
        return supabaseService.findAllCategories();
    }
}
