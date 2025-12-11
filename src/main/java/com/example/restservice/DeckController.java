package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    /**
     * Request body for deck creation with visited place IDs
     */
    public static class DeckCreationRequest {
        private List<String> visitedPlaceIds;

        public List<String> getVisitedPlaceIds() {
            return visitedPlaceIds;
        }

        public void setVisitedPlaceIds(List<String> visitedPlaceIds) {
            this.visitedPlaceIds = visitedPlaceIds;
        }
    }

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    /**
     * Get all available categories
     */
    @GetMapping("/categories")
    public List<String> getCategories() {
        return deckService.getAllCategories();
    }

    /**
     * Get all decks for a user
     */
    @GetMapping("/user/{userId}")
    public List<DeckDTO> getUserDecks(@PathVariable String userId) {
        return deckService.getAllDecksForUser(userId);
    }

    /**
     * Get a specific deck by ID
     */
    @GetMapping("/{deckId}")
    public ResponseEntity<DeckDTO> getDeckById(@PathVariable Long deckId) {
        return deckService.getDeckById(deckId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new deck for a user
     */
    @PostMapping
    public ResponseEntity<DeckDTO> createDeck(
            @RequestParam String userId, 
            @RequestParam String category,
            @RequestBody(required = false) DeckCreationRequest request) {
        try {
            List<String> visitedPlaceIds = (request != null && request.getVisitedPlaceIds() != null) 
                    ? request.getVisitedPlaceIds() 
                    : new ArrayList<>();
            DeckDTO deck = deckService.createDeck(userId, category, visitedPlaceIds);
            return ResponseEntity.status(HttpStatus.CREATED).body(deck);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a deck
     */
    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long deckId) {
        boolean deleted = deckService.deleteDeck(deckId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
