package com.instagram.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-data-management-service", url = "http://localhost:8000")
public interface UserDataManagementClient {

    @GetMapping("/api/v1/user-management/getUserId/{username}")
    Long getUserIdByUsername(@PathVariable String username);
}
