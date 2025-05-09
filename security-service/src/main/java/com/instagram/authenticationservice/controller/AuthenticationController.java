package com.instagram.authenticationservice.controller;

import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.authenticationservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public String registerNewUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            authenticationService.registerNewUser(userRegistrationDto);
            log.info("Registered new user: {}", userRegistrationDto);
            return "✅ User registered successfully";
        } catch (Exception ex) {
            return "❌ User registration failed. Message: " + ex.getMessage();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody UserAuthenticationDto userAuthenticationDto) {
        try {
            return authenticationService.authenticateUser(userAuthenticationDto);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @GetMapping("/findToken/{username}")
    public String findTokenByUsername(@PathVariable String username) {
        return authenticationService.findTokenByUsername(username);
    }
}
