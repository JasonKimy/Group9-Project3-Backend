package com.example.restservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    
    // Find places by category
    List<Place> findByCategory(String category);
    
    // Find places by city
    List<Place> findByCity(String city);
    
    // Find places by category and city
    List<Place> findByCategoryAndCity(String category, String city);
    
    // Find places by subcategory
    List<Place> findBySubcategory(String subcategory);
    
    // Search places by name (case insensitive)
    List<Place> findByNameContainingIgnoreCase(String name);
    
    // Custom query to find places within a certain distance (approximate bounding box)
    @Query("SELECT p FROM Place p WHERE " +
           "p.lat BETWEEN :minLat AND :maxLat AND " +
           "p.lon BETWEEN :minLon AND :maxLon")
    List<Place> findPlacesInBoundingBox(
        @Param("minLat") Double minLat,
        @Param("maxLat") Double maxLat,
        @Param("minLon") Double minLon,
        @Param("maxLon") Double maxLon
    );
    
    // Search in description
    @Query("SELECT p FROM Place p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Place> searchByDescription(@Param("keyword") String keyword);
    
    // Find all distinct categories
    @Query("SELECT DISTINCT p.category FROM Place p ORDER BY p.category")
    List<String> findAllCategories();
    
    // Find all distinct cities
    @Query("SELECT DISTINCT p.city FROM Place p ORDER BY p.city")
    List<String> findAllCities();
}
