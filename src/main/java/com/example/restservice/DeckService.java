package com.example.restservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeckService {

    private final SupabaseService supabaseService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private static final String TABLE_NAME = "decks";

    public DeckService(SupabaseService supabaseService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.supabaseService = supabaseService;
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
     * Get all decks for a user
     */
    public List<DeckDTO> getAllDecksForUser(String userId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("user_id", "eq." + userId)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<Deck> decks = objectMapper.readValue(response.getBody(), new TypeReference<List<Deck>>() {});
            return decks.stream()
                    .map(this::convertToDeckDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching decks for user: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get a specific deck by ID
     */
    public Optional<DeckDTO> getDeckById(Long deckId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("id", "eq." + deckId)
                    .queryParam("limit", "1")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<Deck> decks = objectMapper.readValue(response.getBody(), new TypeReference<List<Deck>>() {});
            if (decks.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(convertToDeckDTO(decks.get(0)));
        } catch (Exception e) {
            System.err.println("Error fetching deck by ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Create a new deck for a user with 3 random places from a category
     */
    public DeckDTO createDeck(String userId, String category) {
        // Get all places in the category
        List<Place> allPlaces = supabaseService.findByCategory(category);
        
        if (allPlaces.size() < 3) {
            throw new IllegalArgumentException("Not enough places in category: " + category);
        }

        // Randomly select 3 places
        Collections.shuffle(allPlaces);
        List<Place> selectedPlaces = allPlaces.subList(0, 3);

        // Create the deck
        Deck deck = new Deck(
                userId,
                selectedPlaces.get(0).getId(),
                selectedPlaces.get(1).getId(),
                selectedPlaces.get(2).getId(),
                category
        );

        try {
            String json = objectMapper.writeValueAsString(deck);
            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    getTableUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            List<Deck> createdDecks = objectMapper.readValue(response.getBody(), new TypeReference<List<Deck>>() {});
            if (createdDecks.isEmpty()) {
                throw new RuntimeException("Failed to create deck");
            }
            return convertToDeckDTO(createdDecks.get(0));
        } catch (Exception e) {
            System.err.println("Error creating deck: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create deck", e);
        }
    }

    /**
     * Delete a deck
     */
    public boolean deleteDeck(Long deckId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(getTableUrl())
                    .queryParam("id", "eq." + deckId)
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
            System.err.println("Error deleting deck: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all available categories
     */
    public List<String> getAllCategories() {
        return supabaseService.findAllCategories();
    }

    /**
     * Convert Deck entity to DeckDTO with actual Place objects
     */
    private DeckDTO convertToDeckDTO(Deck deck) {
        List<Place> places = new ArrayList<>();
        
        // Fetch the three places
        if (deck.getPlace1Id() != null) {
            supabaseService.findById(deck.getPlace1Id()).ifPresent(places::add);
        }
        if (deck.getPlace2Id() != null) {
            supabaseService.findById(deck.getPlace2Id()).ifPresent(places::add);
        }
        if (deck.getPlace3Id() != null) {
            supabaseService.findById(deck.getPlace3Id()).ifPresent(places::add);
        }

        return new DeckDTO(
                deck.getId(),
                deck.getCreatedAt(),
                deck.getUserId(),
                deck.getCategory(),
                places
        );
    }
}
