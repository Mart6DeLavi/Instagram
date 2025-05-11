package com.instagram.profileservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "follow-service", url = "http://follow-service:8007")
public interface FollowServiceClient {

    @GetMapping("/api/v1/follow/followers/{userId}")
    ResponseEntity<List<Long>> getFollowers(@PathVariable Long userId);

    @GetMapping("/api/v1/follow/following/{userId}")
    ResponseEntity<List<Long>> getFollowing(@PathVariable Long userId);
}
