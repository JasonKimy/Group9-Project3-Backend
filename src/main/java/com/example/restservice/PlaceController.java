package com.example.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*")
public class PlaceController {
    
    @Autowired
    private PlaceRepository placeRepository;
    
    /**
     * Get all places
     */
    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Get a specific place by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable String id) {
        return placeRepository.findById(id)
                .map(place -> ResponseEntity.ok(new PlaceDTO(place)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get places by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<PlaceDTO>> getPlacesByCategory(@PathVariable String category) {
        List<Place> places = placeRepository.findByCategory(category);
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Get places by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<PlaceDTO>> getPlacesByCity(@PathVariable String city) {
        List<Place> places = placeRepository.findByCity(city);
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Get places by subcategory
     */
    @GetMapping("/subcategory/{subcategory}")
    public ResponseEntity<List<PlaceDTO>> getPlacesBySubcategory(@PathVariable String subcategory) {
        List<Place> places = placeRepository.findBySubcategory(subcategory);
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Search places by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<PlaceDTO>> searchPlacesByName(@RequestParam String name) {
        List<Place> places = placeRepository.findByNameContainingIgnoreCase(name);
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Search places by keyword in description
     */
    @GetMapping("/search/description")
    public ResponseEntity<List<PlaceDTO>> searchPlacesByDescription(@RequestParam String keyword) {
        List<Place> places = placeRepository.searchByDescription(keyword);
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Get places within a bounding box (for map filtering)
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<PlaceDTO>> getPlacesInBoundingBox(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon) {
        List<Place> places = placeRepository.findPlacesInBoundingBox(minLat, maxLat, minLon, maxLon);
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Filter places by category and city
     */
    @GetMapping("/filter")
    public ResponseEntity<List<PlaceDTO>> filterPlaces(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city) {
        List<Place> places;
        
        if (category != null && city != null) {
            places = placeRepository.findByCategoryAndCity(category, city);
        } else if (category != null) {
            places = placeRepository.findByCategory(category);
        } else if (city != null) {
            places = placeRepository.findByCity(city);
        } else {
            places = placeRepository.findAll();
        }
        
        List<PlaceDTO> placeDTOs = places.stream()
                .map(PlaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeDTOs);
    }
    
    /**
     * Get all available categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = placeRepository.findAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get all available cities
     */
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = placeRepository.findAllCities();
        return ResponseEntity.ok(cities);
    }
    
    /**
     * Get count of all places
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getPlaceCount() {
        long count = placeRepository.count();
        return ResponseEntity.ok(count);
    }
}
