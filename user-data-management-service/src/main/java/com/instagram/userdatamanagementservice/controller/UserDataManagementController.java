package com.instagram.userdatamanagementservice.controller;

import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.userdatamanagementservice.model.UserExists;
import com.instagram.userdatamanagementservice.service.UserDataManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-management")
public class UserDataManagementController {
    private final UserDataManagementService userDataManagementService;

    @PostMapping("/authenticate")
    public UserExists authenticateUser(@RequestBody UserAuthenticationDto userAuthenticationDto) {
        return userDataManagementService.authenticateUser(userAuthenticationDto);
    }
}
