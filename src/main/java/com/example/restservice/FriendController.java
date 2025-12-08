package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * Get all users
     * GET /api/friends
     */
    @GetMapping
    public ResponseEntity<List<Friend>> getAllFriends() {
        List<Friend> friends = friendService.findAll();
        return ResponseEntity.ok(friends);
    }

    /**
     * Send a friend request
     * POST /api/friends/request
     * Body: { "senderId": "uuid", "receiverId": "uuid" }
     */
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody Map<String, String> request) {
        try {
            String senderId = request.get("senderId");
            String receiverId = request.get("receiverId");
            
            Friend friendRequest = friendService.sendFriendRequest(senderId, receiverId);
            return ResponseEntity.ok(friendRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Accept a friend request
     * POST /api/friends/accept/{requestId}
     * Body: { "accepterId": "uuid" }
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(
            @PathVariable String requestId,
            @RequestBody Map<String, String> request) {
        try {
            String accepterId = request.get("accepterId");
            boolean success = friendService.acceptFriendRequest(requestId, accepterId);
            
            if (success) {
                return ResponseEntity.ok("Friend request accepted");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Reject a friend request
     * POST /api/friends/reject/{requestId}
     * Body: { "rejecterId": "uuid" }
     */
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectFriendRequest(
            @PathVariable String requestId,
            @RequestBody Map<String, String> request) {
        try {
            String rejecterId = request.get("rejecterId");
            boolean success = friendService.rejectFriendRequest(requestId, rejecterId);
            
            if (success) {
                return ResponseEntity.ok("Friend request rejected");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Reject and block a user from a friend request
     * POST /api/friends/reject-and-block/{requestId}
     * Body: { "blockerId": "uuid" }
     */
    @PostMapping("/reject-and-block/{requestId}")
    public ResponseEntity<?> rejectAndBlockRequest(
            @PathVariable String requestId,
            @RequestBody Map<String, String> request) {
        try {
            String blockerId = request.get("blockerId");
            boolean success = friendService.rejectAndBlockRequest(requestId, blockerId);
            
            if (success) {
                return ResponseEntity.ok("User blocked");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Block a user
     * POST /api/friends/block
     * Body: { "blockerId": "uuid", "blockedUserId": "uuid" }
     */
    @PostMapping("/block")
    public ResponseEntity<?> blockUser(@RequestBody Map<String, String> request) {
        try {
            String blockerId = request.get("blockerId");
            String blockedUserId = request.get("blockedUserId");
            
            boolean success = friendService.blockUser(blockerId, blockedUserId);
            
            if (success) {
                return ResponseEntity.ok("User blocked");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to block user");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Unfriend a user
     * DELETE /api/friends/{friendId}
     * Body: { "userId": "uuid" }
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> unfriend(
            @PathVariable String friendId,
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            boolean success = friendService.unfriend(userId, friendId);
            
            if (success) {
                return ResponseEntity.ok("Friend removed");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove friend");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Unblock a user
     * DELETE /api/friends/unblock/{friendId}
     * Body: { "userId": "uuid" }
     */
    @DeleteMapping("/unblock/{friendId}")
    public ResponseEntity<?> unblock(
            @PathVariable String friendId,
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            boolean success = friendService.unblockUser(userId, friendId);
            
            if (success) {
                return ResponseEntity.ok("User unblocked");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unblock user");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get all friends for a user
     * GET /api/friends/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Friend>> getFriends(@PathVariable String userId) {
        try {
            List<Friend> friends = friendService.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get all sent friend requests for a user
     * GET /api/friends/sent/{userId}
     */
    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<Friend>> getSentRequests(@PathVariable String userId) {
        try {
            List<Friend> sentRequests = friendService.getSentRequests(userId);
            return ResponseEntity.ok(sentRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get all incoming friend requests for a user
     * GET /api/friends/incoming/{userId}
     */
    @GetMapping("/incoming/{userId}")
    public ResponseEntity<List<Friend>> getIncomingRequests(@PathVariable String userId) {
        try {
            List<Friend> incomingRequests = friendService.getIncomingRequests(userId);
            return ResponseEntity.ok(incomingRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get all users blocked by a user
     * GET /api/friends/blocked/{userId}
     */
    @GetMapping("/blocked/{userId}")
    public ResponseEntity<List<Friend>> getBlocks(@PathVariable String userId) {
        try {
            List<Friend> blocks = friendService.getBlocks(userId);
            return ResponseEntity.ok(blocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}