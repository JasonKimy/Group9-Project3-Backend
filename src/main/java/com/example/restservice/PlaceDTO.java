package com.example.restservice;

import java.util.List;

public class PlaceDTO {
    
    private String id;
    private String name;
    private String category;
    private String subcategory;
    private Double lat;
    private Double lon;
    private String city;
    private String county;
    private String state;
    private List<String> tags;
    private String description;
    
    // Default constructor
    public PlaceDTO() {
    }
    
    // Constructor from Place entity
    public PlaceDTO(Place place) {
        this.id = place.getId();
        this.name = place.getName();
        this.category = place.getCategory();
        this.subcategory = place.getSubcategory();
        this.lat = place.getLat();
        this.lon = place.getLon();
        this.city = place.getCity();
        this.county = place.getCounty();
        this.state = place.getState();
        this.tags = place.getTags();
        this.description = place.getDescription();
    }
    
    // Full constructor
    public PlaceDTO(String id, String name, String category, String subcategory,
                    Double lat, Double lon, String city, String county, String state,
                    List<String> tags, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.subcategory = subcategory;
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.county = county;
        this.state = state;
        this.tags = tags;
        this.description = description;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubcategory() {
        return subcategory;
    }
    
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    
    public Double getLat() {
        return lat;
    }
    
    public void setLat(Double lat) {
        this.lat = lat;
    }
    
    public Double getLon() {
        return lon;
    }
    
    public void setLon(Double lon) {
        this.lon = lon;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCounty() {
        return county;
    }
    
    public void setCounty(String county) {
        this.county = county;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
