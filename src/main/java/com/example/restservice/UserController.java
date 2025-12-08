package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new user
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Check if username already exists
        if (userService.usernameExists(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Check if email already exists
        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Note: In production, you should hash the password here before creating the user
        User createdUser = userService.createUser(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFavChallenge1(),
                user.getFavChallenge2()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Update user
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        Optional<User> updatedUser = userService.updateUser(id, user);
        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Check if username exists
     * GET /api/users/check/username/{username}
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }

    /**
     * Check if email exists
     * GET /api/users/check/email/{email}
     */
    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get user's favorite challenges
     * GET /api/users/{id}/favorites
     */
    @GetMapping("/{id}/favorites")
    public ResponseEntity<FavoriteChallengesDTO> getFavoriteChallenges(@PathVariable String id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        FavoriteChallengesDTO dto = new FavoriteChallengesDTO(
            user.get().getFavChallenge1(),
            user.get().getFavChallenge2()
        );
        return ResponseEntity.ok(dto);
    }

    /**
     * Update favorite challenge 1
     * PATCH /api/users/{id}/favorite1
     */
    @PatchMapping("/{id}/favorite1")
    public ResponseEntity<User> updateFavChallenge1(
            @PathVariable String id, 
            @RequestBody FavoriteChallengeRequest request) {
        Optional<User> updatedUser = userService.updateFavChallenge1(id, request.getChallengeId());
        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update favorite challenge 2
     * PATCH /api/users/{id}/favorite2
     */
    @PatchMapping("/{id}/favorite2")
    public ResponseEntity<User> updateFavChallenge2(
            @PathVariable String id, 
            @RequestBody FavoriteChallengeRequest request) {
        Optional<User> updatedUser = userService.updateFavChallenge2(id, request.getChallengeId());
        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update both favorite challenges
     * PATCH /api/users/{id}/favorites
     */
    @PatchMapping("/{id}/favorites")
    public ResponseEntity<User> updateFavoriteChallenges(
            @PathVariable String id, 
            @RequestBody FavoriteChallengesDTO request) {
        Optional<User> updatedUser = userService.updateFavChallenges(
            id, 
            request.getFavChallenge1(), 
            request.getFavChallenge2()
        );
        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Inner classes for request/response DTOs
    public static class FavoriteChallengeRequest {
        private String challengeId;

        public FavoriteChallengeRequest() {}

        public String getChallengeId() { return challengeId; }
        public void setChallengeId(String challengeId) { this.challengeId = challengeId; }
    }

    public static class FavoriteChallengesDTO {
        private String favChallenge1;
        private String favChallenge2;

        public FavoriteChallengesDTO() {}

        public FavoriteChallengesDTO(String favChallenge1, String favChallenge2) {
            this.favChallenge1 = favChallenge1;
            this.favChallenge2 = favChallenge2;
        }

        public String getFavChallenge1() { return favChallenge1; }
        public void setFavChallenge1(String favChallenge1) { this.favChallenge1 = favChallenge1; }

        public String getFavChallenge2() { return favChallenge2; }
        public void setFavChallenge2(String favChallenge2) { this.favChallenge2 = favChallenge2; }
    }
}
