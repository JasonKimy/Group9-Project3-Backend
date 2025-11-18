package com.example.restservice;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping
    public List<String> getCategories() {
        return deckService.getAllCategories();
    }

    @GetMapping("/{category}/{userId}")
    public Deck getDeck(@PathVariable String category, @PathVariable String userId) {
        return deckService.getDeckForUser(category, userId);
    }
}
