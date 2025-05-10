package com.instagram.profileservice.controller;

import com.instagram.dto.AllProfileInformationDto;
import com.instagram.dto.feign.ProfileInformationOfSubscriptionsDto;
import com.instagram.profileservice.dto.UserProfileUpdateInformationDto;
import com.instagram.profileservice.entity.UserProfile;
import com.instagram.profileservice.mapper.EntityMapper;
import com.instagram.profileservice.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<AllProfileInformationDto>> createProfile(
            @RequestPart("username") String username,
            @RequestPart("aboutMyself") String aboutMyself,
            @RequestPart("avatar") MultipartFile avatar
    ) {
        return profileService.createProfileAsync(username, aboutMyself, avatar)
                .thenApply(profile -> ResponseEntity.ok(EntityMapper.mapToProfileInformationDto(profile)))
                .exceptionally(ex -> ResponseEntity.internalServerError().body(null));
    }

    @PostMapping("/subscriptions-info")
    public ResponseEntity<?> getProfilesOfSubscriptions(@RequestBody List<Long> userIds) {
        return ResponseEntity.ok(profileService.getAllProfileInformationOfSubscriptions(userIds));
    }

    @GetMapping("/get/{username}")
    public AllProfileInformationDto getProfile(@PathVariable String username) {
        return profileService.getAllProfileInformation(username);
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<ProfileInformationOfSubscriptionsDto>> getFollowersInfo(@PathVariable String username) {
        List<ProfileInformationOfSubscriptionsDto> followers = profileService.getFollowersInfo(username);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<ProfileInformationOfSubscriptionsDto>> getFollowingInfo(@PathVariable String username) {
        List<ProfileInformationOfSubscriptionsDto> following = profileService.getFollowingInfo(username);
        return ResponseEntity.ok(following);
    }


    @PatchMapping("/update/{username}")
    public CompletableFuture<ResponseEntity<UserProfile>> updateProfileInformation(
            @PathVariable String username,
            @RequestBody @Valid UserProfileUpdateInformationDto dto
    ) {
        return profileService.updateProfileAsync(username, dto)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }
    @DeleteMapping("/delete/{username}")
    public CompletableFuture<ResponseEntity<String>> deleteProfile(@PathVariable String username) {
        return profileService.deleteProfileAsync(username)
                .thenApply(v -> ResponseEntity.ok("✅ Deleted"))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ " + ex.getMessage()));
    }
}
