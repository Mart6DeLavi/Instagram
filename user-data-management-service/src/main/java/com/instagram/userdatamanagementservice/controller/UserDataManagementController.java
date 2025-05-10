package com.instagram.userdatamanagementservice.controller;

import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.userdatamanagementservice.model.UserExists;
import com.instagram.userdatamanagementservice.service.UserDataManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-management")
public class UserDataManagementController {

    private final UserDataManagementService userDataManagementService;

    @PostMapping("/authenticate")
    public UserExists authenticateUser(@RequestBody UserAuthenticationDto dto) {
        return userDataManagementService.authenticateUser(dto);
    }

    @GetMapping("/getUserId/{username}")
    public Long getUserIdByUsername(@PathVariable String username) {
        return userDataManagementService.getUserIdByUsername(username);
    }

    @GetMapping("/getUsername/{userId}")
    public String getUsernameByUserId(@PathVariable Long userId) {
        return userDataManagementService.getUsernameByUserId(userId);
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<String>> registerUser(@RequestBody UserRegistrationDto dto) {
        return userDataManagementService.createNewUser(dto)
                .thenApply(v -> ResponseEntity.ok("User registered successfully"))
                .exceptionally(ex -> ResponseEntity.badRequest().body("❌ Error: " + ex.getCause().getMessage()));
    }

    @PutMapping("/update")
    public CompletableFuture<ResponseEntity<String>> updateUser(@RequestBody UserRegistrationDto dto) {
        return userDataManagementService.updateUserInformationAsync(dto)
                .thenApply(status -> ResponseEntity.ok("User updated: " + status.name()))
                .exceptionally(ex -> ResponseEntity.badRequest().body("❌ Error: " + ex.getCause().getMessage()));
    }

    @DeleteMapping("/delete/{username}")
    public CompletableFuture<ResponseEntity<String>> deleteUser(@PathVariable String username) {
        return userDataManagementService.deleteUserAsync(username)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("❌ Error: " + ex.getCause().getMessage()));
    }
}

