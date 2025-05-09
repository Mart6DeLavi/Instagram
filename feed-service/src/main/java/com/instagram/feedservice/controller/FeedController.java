package com.instagram.feedservice.controller;

import com.instagram.dto.PostInformationDto;
import com.instagram.feedservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/{username}")
    public List<PostInformationDto> getAllPostsFromUsersFollowingByUsername(@PathVariable String username) {
        return feedService.getAllPostsFromUsersFollowingByUsername(username);
    }
}
