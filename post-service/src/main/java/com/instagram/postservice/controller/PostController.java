package com.instagram.postservice.controller;

import com.instagram.dto.PostInformationDto;
import com.instagram.postservice.dto.UpdatePostInformationDto;
import com.instagram.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/all/{username}")
    public ResponseEntity<List<PostInformationDto>> getAllPosts(@PathVariable String username) {
        return ResponseEntity.ok(postService.getAllPostsByUsername(username));
    }

    @GetMapping("/allById/{userId}")
    public ResponseEntity<List<PostInformationDto>> getAllPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getAllPostsByUserId(userId));
    }

    @GetMapping("/{postId}")
    public boolean isPostExist(@PathVariable String postId) {
        return postService.isPostExist(postId);
    }

    @GetMapping("/post/{postId}")
    public PostInformationDto getPostByPostId(@PathVariable String postId) {
        return postService.getPostById(postId);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<PostInformationDto>> createPost(
            @RequestParam("username") String username,
            @RequestParam("description") String description,
            @RequestParam("tags") List<String> tags,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("locationName") String locationName,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return postService.createNewPostAsync(username, description, tags, latitude, longitude, locationName, files)
                .thenApply(post -> ResponseEntity.status(HttpStatus.CREATED).body(post))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    @PatchMapping("/update")
    public CompletableFuture<ResponseEntity<PostInformationDto>> updatePost(
            @RequestHeader String postId,
            @RequestBody UpdatePostInformationDto dto
    ) {
        return postService.updatePostAsync(postId, dto)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
