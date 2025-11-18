package com.example.restservice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public CheckIn createCheckIn(@RequestBody CheckIn checkIn) {
        return checkInService.createCheckIn(checkIn.getUserId(), checkIn.getPlaceId(), checkIn.getPhotoUri());
    }
}
