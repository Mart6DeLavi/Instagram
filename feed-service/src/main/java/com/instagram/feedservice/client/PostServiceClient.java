package com.instagram.feedservice.client;

import com.instagram.dto.PostInformationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "post-service", url = "http://localhost:8004")
public interface PostServiceClient {

    @GetMapping("/api/v1/posts/allById/{userId}")
    ResponseEntity<List<PostInformationDto>> getAllPostsByUserId(@PathVariable Long userId);
}
