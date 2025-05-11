package com.instagram.profileservice.client;

import com.instagram.profileservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-data-management-service", url = "http://user-data-management-service:8000", configuration = FeignConfig.class)
public interface UserDataManagementClient {

    @GetMapping("/api/v1/user-management/getUserId/{username}")
    Long getUserIdByUsername(@PathVariable String username);
}
