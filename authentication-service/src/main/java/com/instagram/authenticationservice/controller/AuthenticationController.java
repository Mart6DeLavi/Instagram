package com.instagram.authenticationservice.controller;

import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.authenticationservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public String registerNewUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            authenticationService.registerNewUser(userRegistrationDto);
            log.info("Registered new user: {}", userRegistrationDto);
            return "✅ User registered successfully";
        } catch (Exception ex) {
            return "❌ User registration failed. Message: " + ex.getMessage();
        }
    }

    @PostMapping("/authenticate")
    public String authenticateUser(UserAuthenticationDto userAuthenticationDto) {
        try {
            return authenticationService.authenticateUser(userAuthenticationDto);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
