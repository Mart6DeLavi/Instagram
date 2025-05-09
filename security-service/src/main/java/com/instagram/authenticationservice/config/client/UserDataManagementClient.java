package com.instagram.authenticationservice.config.client;

import com.instagram.authenticationservice.model.UserExists;
import com.instagram.dto.kafka.UserAuthenticationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-data-management-service", url = "http://localhost:8000")
public interface UserDataManagementClient {

    @PostMapping("/api/v1/user-management/authenticate")
    UserExists authenticateUser(@RequestBody UserAuthenticationDto userAuthenticationDto);
}
