package com.instagram.postservice.controller;

import com.instagram.postservice.dto.PostInformationDto;
import com.instagram.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/all/{username}")
    public ResponseEntity<List<PostInformationDto>> getAllPosts(@PathVariable  String username) {
        return ResponseEntity.ok(postService.getAllPostsByUsername(username));
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostInformationDto> createPost(
            @RequestParam("username") String username,
            @RequestParam("description") String description,
            @RequestParam("tags") List<String> tags,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("locationName") String locationName,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createNewPost(username, description, tags, latitude, longitude, locationName, files));
    }
}
