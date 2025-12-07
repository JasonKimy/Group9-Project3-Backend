package com.example.restservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
// import java.util.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private static final String TABLE_NAME = "friends";

    public FriendService(RestTemplate restTemplate, ObjectMapper objectMapper) {
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

    public List<Friend> findAll() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    getTableUrl(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<List<Friend>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching all friends: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Send a friend request
     * Creates one row with sender as friend_1_id and receiver as friend_2_id with status "pending"
     */
    public Friend sendFriendRequest(String senderId, String receiverId) {
        // Check if any relationship already exists
        if (relationshipExists(senderId, receiverId)) {
            throw new RuntimeException("Relationship already exists between these users");
        }

        if (receiverId.equals(senderId)) {
            throw new RuntimeException("Cannot send friend request to oneself");
        }

        if (receiverId.equals(null)) {
            throw new RuntimeException("Receiver ID is invalid");
        }

        Friend friendRequest = new Friend(
                senderId,
                receiverId,
                "pending"
        );

        try {
            String json = objectMapper.writeValueAsString(friendRequest);
            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    getTableUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            List<Friend> friends = objectMapper.readValue(response.getBody(), new TypeReference<List<Friend>>() {});
            return friends.isEmpty() ? friendRequest : friends.get(0);
        } catch (Exception e) {
            System.err.println("Error sending friend request: " + e.getMessage());
            e.printStackTrace();
            return friendRequest;
        }
    }

    /**
 * Accept a friend request
 * Deletes the pending request and creates two mirrored "friends" rows
 */
public boolean acceptFriendRequest(String requestId, String accepterId) {
    try {
        // First, get the original request to validate
        Optional<Friend> requestOpt = findById(requestId);
        if (requestOpt.isEmpty()) {
            return false;
        }

        Friend request = requestOpt.get();
        
        // Validate that the accepter is the friend_id in the request
        if (!request.getFriend_2_id().equals(accepterId)) {
            throw new RuntimeException("User not authorized to accept this request");
        }

        // Validate status is pending
        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("Request is not in pending status");
        }

        // Delete the pending request
        String deleteUrl = UriComponentsBuilder.fromUriString(getTableUrl())
                .queryParam("id", "eq." + requestId)
                .toUriString();

        HttpEntity<String> deleteEntity = new HttpEntity<>(createHeaders());
        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteEntity, String.class);

        // Create first friendship row
        Friend friendship1 = new Friend(
                // UUID.randomUUID().toString(),
                request.getFriend_1_id(),
                request.getFriend_2_id(),
                "friends"
        );

        String json1 = objectMapper.writeValueAsString(friendship1);
        HttpEntity<String> entity1 = new HttpEntity<>(json1, createHeaders());
        restTemplate.exchange(getTableUrl(), HttpMethod.POST, entity1, String.class);

        // Create mirror friendship row
        Friend friendship2 = new Friend(
            // UUID.randomUUID().toString(),
            request.getFriend_2_id(),
            request.getFriend_1_id(),
            "friends"
    );

        String json2 = objectMapper.writeValueAsString(friendship2);
        HttpEntity<String> entity2 = new HttpEntity<>(json2, createHeaders());
        restTemplate.exchange(getTableUrl(), HttpMethod.POST, entity2, String.class);

        return true;
    } catch (Exception e) {
        System.err.println("Error accepting friend request: " + e.getMessage());
        return false;
    }
}

    /**
     * Reject a friend request
     * Deletes existing row
     */
    public boolean rejectFriendRequest(String requestId, String rejecterId) {
        try {
            // First validate the request exists and user is authorized
            Optional<Friend> requestOpt = findById(requestId);
            if (requestOpt.isEmpty()) {
                return false;
            }

            Friend request = requestOpt.get();
            if (!request.getFriend_2_id().equals(rejecterId)) {
                throw new RuntimeException("User not authorized to reject this request");
            }

            String url = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("id", "eq." + requestId)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error rejecting friend request: " + e.getMessage());
            return false;
        }
    }

    /**
     * Block a user
     * Deletes existing rows and creates a new row with status "blocked"
     */
    public boolean blockUser(String blockerId, String blockedId) {
        try {
            if (blockerId.equals(blockedId)) {
                throw new RuntimeException("User not authorized to block this user");
            }
            // Delete any existing friendship rows in both directions if not blocked themselves
            removeFriend(blockerId, blockedId);

            // Create blocked relationship
            Friend blockedRelationship = new Friend(
                    // UUID.randomUUID().toString(),
                    blockerId,
                    blockedId,
                    "blocked"
            );

            String json = objectMapper.writeValueAsString(blockedRelationship);
            HttpEntity<String> entity = new HttpEntity<>(json, createHeaders());
            
            restTemplate.exchange(getTableUrl(), HttpMethod.POST, entity, String.class);

            return true;
        } catch (Exception e) {
            System.err.println("Error blocking user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reject and block from a friend request
     * Deletes existing row and creates a new row with status "blocked"
     */
    public boolean rejectAndBlockRequest(String requestId, String blockerId) {
        try {
            // Get the request to find out who to block
            Optional<Friend> requestOpt = findById(requestId);
            if (requestOpt.isEmpty()) {
                return false;
            }

            Friend request = requestOpt.get();
            if (!request.getFriend_2_id().equals(blockerId)) {
                throw new RuntimeException("User not authorized to block this user");
            }

            String blockedUserId = request.getFriend_1_id();

            // Delete the pending request
            rejectFriendRequest(requestId, blockerId);

            // Block the user
            return blockUser(blockerId, blockedUserId);
        } catch (Exception e) {
            System.err.println("Error blocking user from request: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unfriend a user
     * Deletes existing rows
     */
    public boolean unfriend(String userId, String friendId) {
        try {
            return removeFriend(userId, friendId);
        } catch (Exception e) {
            System.err.println("Error unfriending user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all friends for a user
     */
    public List<Friend> getFriends(String userId) {
        try {
            String url = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + userId)
                    .queryParam("status", "eq.friends")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<List<Friend>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching friends: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get all sent friend requests for a user
     */
    public List<Friend> getSentRequests(String userId) {
        try {
            String url = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + userId)
                    .queryParam("status", "eq.pending")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<List<Friend>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching sent requests: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get all incoming friend requests for a user
     */
    public List<Friend> getIncomingRequests(String userId) {
        try {
            String url = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_2_id", "eq." + userId)
                    .queryParam("status", "eq.pending")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<List<Friend>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching incoming requests: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get all sent friend requests for a user
     */
    public List<Friend> getBlocks(String userId) {
        try {
            String url = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + userId)
                    .queryParam("status", "eq.blocked")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<List<Friend>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching blocked users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Helper: Remove friendship between two users
     * Deletes existing rows, ignores if blocked status
     */
    private boolean removeFriend(String userId, String friendId) {
        try {
            // Delete where user_id = userId AND friend_id = friendId AND status != 'blocked'
            String url1 = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + userId)
                    .queryParam("friend_2_id", "eq." + friendId)
                    .queryParam("status", "neq.blocked")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(url1, HttpMethod.DELETE, entity, String.class);

            // Delete where user_id = friendId AND friend_id = userId AND status != 'blocked'
            String url2 = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + friendId)
                    .queryParam("friend_2_id", "eq." + userId)
                    .queryParam("status", "neq.blocked")
                    .toUriString();

            restTemplate.exchange(url2, HttpMethod.DELETE, entity, String.class);

            return true;
        } catch (Exception e) {
            System.err.println("Error removing friend: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unblock a user
     * Deletes existing row in one direction with blocked status
     */
    public boolean unblockUser(String userId, String friendId) {
        try {
            // Delete where user_id = userId AND friend_id = friendId AND status == 'blocked'
            String url1 = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + userId)
                    .queryParam("friend_2_id", "eq." + friendId)
                    .queryParam("status", "eq.blocked")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(url1, HttpMethod.DELETE, entity, String.class);

            return true;
        } catch (Exception e) {
            System.err.println("Error unblocking user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper: Check if any existing rows exist between two users
     */
    private boolean relationshipExists(String userId, String friendId) {
        try {
            // Check if userId -> friendId exists
            String url1 = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + userId)
                    .queryParam("friend_2_id", "eq." + friendId)
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response1 = restTemplate.exchange(
                    url1,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<Friend> direction1 = objectMapper.readValue(response1.getBody(), new TypeReference<List<Friend>>() {});
            if (!direction1.isEmpty()) {
                return true;
            }

            // Check if friendId -> userId exists
            String url2 = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("friend_1_id", "eq." + friendId)
                    .queryParam("friend_2_id", "eq." + userId)
                    .toUriString();

            ResponseEntity<String> response2 = restTemplate.exchange(
                    url2,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<Friend> direction2 = objectMapper.readValue(response2.getBody(), new TypeReference<List<Friend>>() {});
            return !direction2.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking relationship: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper: Find a friend relationship by ID
     */
    private Optional<Friend> findById(String id) {
        try {
            String url = UriComponentsBuilder.fromUriString(getTableUrl())
                    .queryParam("id", "eq." + id)
                    .queryParam("limit", "1")
                    .toUriString();

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<Friend> friends = objectMapper.readValue(response.getBody(), new TypeReference<List<Friend>>() {});
            return friends.isEmpty() ? Optional.empty() : Optional.of(friends.get(0));
        } catch (Exception e) {
            System.err.println("Error fetching friend by ID: " + e.getMessage());
            return Optional.empty();
        }
    }
}