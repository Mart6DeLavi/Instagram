package com.instagram.profileservice.controller;

import com.instagram.exception.UserNotFoundException;
import com.instagram.profileservice.dto.AllProfileInformationDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AllProfileInformationDto> createProfile(
            @RequestPart("username") String username,
            @RequestPart("aboutMyself") String aboutMyself,
            @RequestPart("avatar") MultipartFile avatar
    ) {
        UserProfile created = profileService.createProfile(username, aboutMyself, avatar);
        return ResponseEntity.ok(EntityMapper.mapToProfileInformationDto(created));
    }

    @GetMapping("/get/{username}")
    public AllProfileInformationDto getProfile(@PathVariable String username) {
        return profileService.getAllProfileInformation(username);
    }

    @PatchMapping("/update/{username}")
    public UserProfile updateProfileInformation(@PathVariable String username, @RequestBody @Valid UserProfileUpdateInformationDto dto) {
        return profileService.updateProfileInformation(username, dto);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteProfile(@PathVariable String username) {
        try {
            profileService.deleteUserProfile(username);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
