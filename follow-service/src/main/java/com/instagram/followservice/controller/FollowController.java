package com.instagram.followservice.controller;

import com.instagram.followservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow")
public class FollowController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public CompletableFuture<ResponseEntity<String>> followUser(
            @RequestParam String followerUsername,
            @RequestParam String followeeUsername
    ) {
        return subscriptionService.makeFollower(followerUsername, followeeUsername)
                .thenApply(v -> ResponseEntity.ok("✅ Subscribed"))
                .exceptionally(ex -> ResponseEntity.badRequest().body("❌ " + ex.getMessage()));
    }

    @DeleteMapping("/unsubscribe")
    public CompletableFuture<ResponseEntity<String>> unfollowUser(
            @RequestParam String followerUsername,
            @RequestParam String followeeUsername
    ) {
        return subscriptionService.deleteFollowing(followerUsername, followeeUsername)
                .thenApply(v -> ResponseEntity.ok("✅ Unsubscribed"))
                .exceptionally(ex -> ResponseEntity.badRequest().body("❌ " + ex.getMessage()));
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
