package com.instagram.profileservice.client;

import com.instagram.profileservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "security-service", url = "http://security-service:8001", configuration = FeignConfig.class)
public interface AuthenticationServiceClient {

    @GetMapping("/api/v1/auth/findToken/{username}")
    String findTokenByUsername(@PathVariable String username);
}
