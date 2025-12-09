package com.example.restservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final UserService userService;
    private final FriendService friendService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    public LeaderboardService(UserService userService, FriendService friendService, 
                             RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.userService = userService;
        this.friendService = friendService;
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

    private String getUsersTableUrl() {
        return supabaseUrl + "/rest/v1/users";
    }

    /**
     * Get top (count) users for leaderboard
     */
    public List<User> getTopUsersByPoints(int count) {
        try {
            String url = UriComponentsBuilder.fromUriString(getUsersTableUrl())
                    .queryParam("order", "points.desc")
                    .queryParam("limit", count)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching top users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get user's global rank
     */
    public int getUserGlobalRank(String userId) {
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return -1;
            }
            long userPoints = userOpt.get().getPoints();

            // Just count number of people above us
            String url = UriComponentsBuilder.fromUriString(getUsersTableUrl())
                    .queryParam("points", "gt." + userPoints)
                    .queryParam("select", "id")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<User> rank = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>() {});
            return rank.size() + 1;
        } catch (Exception e) {
            System.err.println("Error getting user rank: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Get top (count) friends for leaderboard
     */
    public List<User> getTopFriendsByPoints(String userId, int count) {
        try {
            List<Friend> friends = friendService.getFriends(userId);
            
            List<User> friendUsers = new ArrayList<>();
            for (Friend friend : friends) {
                Optional<User> userOpt = userService.findById(friend.getFriend_2_id());
                userOpt.ifPresent(friendUsers::add);
            }
            
            Optional<User> currentUserOpt = userService.findById(userId);
            currentUserOpt.ifPresent(friendUsers::add);
            System.out.println(friendUsers);
            
            return friendUsers.stream()
                    .sorted(Comparator.comparingLong(User::getPoints).reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching top friends: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get user's rank on friends leaderboard
     */
    public int getUserFriendsRank(String userId) {
        try {
            Optional<User> currentUserOpt = userService.findById(userId);
            if (currentUserOpt.isEmpty()) {
                return -1;
            }
            long userPoints = currentUserOpt.get().getPoints();

            List<Friend> friends = friendService.getFriends(userId);
            
            // Just count how many friends are above us
            int rank = 1;
            for (Friend friend : friends) {
                Optional<User> friendOpt = userService.findById(friend.getFriend_2_id());
                if (friendOpt.isPresent() && friendOpt.get().getPoints() > userPoints) {
                    rank++;
                }
            }
            
            return rank;
        } catch (Exception e) {
            System.err.println("Error getting user friends rank: " + e.getMessage());
            return -1;
        }
    }
}