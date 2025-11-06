package com.example.restservice;

import org.springframework.stereotype.Service;

@Service
public class CheckInService {
    private final CheckInRepository checkInRepo;
    private final UserRepository userRepo;

    public CheckInService(CheckInRepository checkInRepo, UserRepository userRepo) {
        this.checkInRepo = checkInRepo;
        this.userRepo = userRepo;
    }

    public CheckIn createCheckIn(String userId, String placeId, double lat, double lon, String photoUrl) {
        User user = userRepo.findById(userId).orElseThrow();
        CheckIn checkIn = new CheckIn(user, placeId, lat, lon, photoUrl);
        user.setPoints(user.getPoints() + 10);
        userRepo.save(user);
        return checkInRepo.save(checkIn);
    }
}
