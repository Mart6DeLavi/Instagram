package com.instagram.followservice.controller;

import com.instagram.followservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow")
public class FollowController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<Void> followUser(
            @RequestParam String followerUsername,
            @RequestParam String followeeUsername
    ) {
        subscriptionService.makeFollower(followerUsername, followeeUsername);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<Void> unfollowUser(
            @RequestParam String followerUsername,
            @RequestParam String followeeUsername
    ) {
        subscriptionService.deleteFollowing(followerUsername, followeeUsername);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<Long>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getAllFollowerIdsByUserId(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<Long>> getFollowings(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getAllFollowingIdsByUserId(userId));
    }
}
