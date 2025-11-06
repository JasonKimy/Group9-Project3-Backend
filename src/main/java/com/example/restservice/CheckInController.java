package com.example.restservice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkins")
@CrossOrigin(origins = "*")
public class CheckInController {
    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public CheckIn checkIn(
            @RequestParam String userId,
            @RequestParam String placeId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String photoUrl
    ) {
        return checkInService.createCheckIn(userId, placeId, latitude, longitude, photoUrl);
    }
}
