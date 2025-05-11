package com.instagram.likeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service", url = "http://post-service:8004")
public interface PostServiceClient {

    @GetMapping("/api/v1/posts/{postId}")
    boolean isPostExist(@PathVariable String postId);
}
