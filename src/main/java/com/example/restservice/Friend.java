package com.example.restservice;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Friend {
    private long id;          // UUID
    private String friend_1_id;     // UUID of the user
    private String friend_2_id;   // UUID of the friend
    private String status;      // pending, friends, blocked

    public Friend() {}

    public Friend(String friend_1_id, String friend_2_id, String status) {
        // this.id = id;
        this.friend_1_id = friend_1_id;
        this.friend_2_id = friend_2_id;
        this.status = status;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public String getFriend_1_id() {
        return friend_1_id;
    }

    public void setFriend_1_id(String friend_1_id) {
        this.friend_1_id = friend_1_id;
    }

    public String getFriend_2_id() {
        return friend_2_id;
    }

    public void setFriend_2_id(String friend_2_id) {
        this.friend_2_id = friend_2_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
}
