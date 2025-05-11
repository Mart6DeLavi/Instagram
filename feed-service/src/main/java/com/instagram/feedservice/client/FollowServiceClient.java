package com.instagram.feedservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "follow-service", url = "http://follow-service:8007")
public interface FollowServiceClient {

    @GetMapping("/api/v1/follow/following/{userId}")
    ResponseEntity<List<Long>> getFollowings(@PathVariable Long userId);
}
