package com.instagram.followservice.client;

import com.instagram.dto.AllProfileInformationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "profile-service", url = "http://profile-service:8002")
public interface ProfileServiceClient {

    @PostMapping("/api/v1/profile/subscriptions-info")
    ResponseEntity<?> getProfilesOfSubscriptions(@RequestBody List<Long> userIds);

    @GetMapping("/api/v1/profile/{username}")
    AllProfileInformationDto getProfile(@PathVariable String username);
}
