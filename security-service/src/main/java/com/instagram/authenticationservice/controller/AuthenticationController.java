package com.instagram.authenticationservice.controller;

import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.authenticationservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<String>> registerNewUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        return authenticationService.registerNewUser(userRegistrationDto)
                .thenApply(v -> {
                    log.info("✅ Registered user asynchronously: {}", userRegistrationDto.username());
                    return ResponseEntity.ok("✅ User registration request sent");
                })
                .exceptionally(ex -> {
                    log.error("❌ Async user registration failed", ex);
                    return ResponseEntity.internalServerError().body("❌ Registration failed: " + ex.getCause().getMessage());
                });
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody UserAuthenticationDto userAuthenticationDto) {
        return authenticationService.authenticateUser(userAuthenticationDto);
    }

    @GetMapping("/findToken/{username}")
    public String findTokenByUsername(@PathVariable String username) {
        return authenticationService.findTokenByUsername(username);
    }
}