package com.instagram.feedservice.controller;

import com.instagram.dto.PostInformationDto;
import com.instagram.feedservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/{username}")
    public CompletableFuture<ResponseEntity<List<PostInformationDto>>> getFeed(@PathVariable String username) {
        return feedService.getAllPostsFromUsersFollowingByUsername(username)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of()));
    }

}
