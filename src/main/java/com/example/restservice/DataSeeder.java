package com.example.restservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//@Component
public class DataSeeder implements CommandLineRunner {
    
    @Autowired
    private SupabaseService supabaseService;
    
    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (supabaseService.count() == 0) {
            seedPlaces();
        } else {
            System.out.println("Database already contains places. Skipping seed.");
        }
    }
    
    private void seedPlaces() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            // Try to load from classpath first (resources folder)
            InputStream inputStream;
            try {
                ClassPathResource resource = new ClassPathResource("monterey_seed_places.json");
                inputStream = resource.getInputStream();
                System.out.println("Loading seed data from classpath...");
            } catch (Exception e) {
                // If not in classpath, try loading from project root
                File file = new File("monterey_seed_places.json");
                if (!file.exists()) {
                    System.err.println("Seed file not found in classpath or project root!");
                    return;
                }
                inputStream = new java.io.FileInputStream(file);
                System.out.println("Loading seed data from project root...");
            }
            
            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode placesNode = rootNode.get("places");
            
            if (placesNode == null || !placesNode.isArray()) {
                System.err.println("Invalid JSON structure: 'places' array not found");
                return;
            }
            
            List<Place> places = new ArrayList<>();
            
            // Process each place
            for (JsonNode placeNode : placesNode) {
                Place place = new Place();
                
                place.setId(placeNode.get("id").asText());
                place.setName(placeNode.get("name").asText());
                place.setCategory(placeNode.get("category").asText());
                
                if (placeNode.has("subcategory")) {
                    place.setSubcategory(placeNode.get("subcategory").asText());
                }
                
                place.setLat(placeNode.get("lat").asDouble());
                place.setLon(placeNode.get("lon").asDouble());
                
                if (placeNode.has("city")) {
                    place.setCity(placeNode.get("city").asText());
                }
                
                if (placeNode.has("county")) {
                    place.setCounty(placeNode.get("county").asText());
                }
                
                if (placeNode.has("state")) {
                    place.setState(placeNode.get("state").asText());
                }
                
                // Parse tags array
                if (placeNode.has("tags")) {
                    List<String> tags = new ArrayList<>();
                    for (JsonNode tagNode : placeNode.get("tags")) {
                        tags.add(tagNode.asText());
                    }
                    place.setTags(tags);
                }
                
                if (placeNode.has("description")) {
                    place.setDescription(placeNode.get("description").asText());
                }
                
                // Parse sources array
                if (placeNode.has("sources")) {
                    List<String> sources = new ArrayList<>();
                    for (JsonNode sourceNode : placeNode.get("sources")) {
                        sources.add(sourceNode.asText());
                    }
                    place.setSources(sources);
                }
                
                places.add(place);
            }
            
            // Save all places to Supabase
            supabaseService.saveAll(places);
            System.out.println("Successfully seeded " + places.size() + " places into Supabase!");
            
        } catch (Exception e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
