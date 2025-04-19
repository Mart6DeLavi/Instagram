package com.instagram.profileservice.controller;

import com.instagram.exception.UserNotFoundException;
import com.instagram.profileservice.dto.AllProfileInformationDto;
import com.instagram.profileservice.dto.UserProfileUpdateInformationDto;
import com.instagram.profileservice.entity.UserProfile;
import com.instagram.profileservice.mapper.EntityMapper;
import com.instagram.profileservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Profile Controller", description = "Handles user profile operations")
public class ProfileController {

    private final ProfileService profileService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AllProfileInformationDto> createProfile(
            @Parameter(description = "User's username", example = "john_doe")
            @RequestPart("username") String username,

            @Parameter(description = "Short description about user", example = "Full-stack developer")
            @RequestPart("aboutMyself") String aboutMyself,

            @Parameter(description = "Avatar image file")
            @RequestPart("avatar") MultipartFile avatar
    ) {
        UserProfile created = profileService.createProfile(username, aboutMyself, avatar);
        return ResponseEntity.ok(EntityMapper.mapToProfileInformationDto(created));
    }

    @Operation(
            summary = "Get profile",
            description = "Returns user profile information by username",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user", example = "john_doe"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile data returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/get/{username}")
    public AllProfileInformationDto getProfile(@PathVariable String username) {
        return profileService.getAllProfileInformation(username);
    }

    @Operation(
            summary = "Update profile",
            description = "Updates user profile info such as description and public flag"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error in request body")
    })
    @PatchMapping("/update/{username}")
    public UserProfile updateProfileInformation(
            @Parameter(description = "User's username", example = "john_doe")
            @PathVariable String username,

            @RequestBody @Valid UserProfileUpdateInformationDto dto
    ) {
        return profileService.updateProfileInformation(username, dto);
    }

    @Operation(
            summary = "Delete profile",
            description = "Deletes a user profile by username",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user", example = "john_doe"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
