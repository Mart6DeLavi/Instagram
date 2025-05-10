package com.instagram.likeservice.controller;

import com.instagram.likeservice.dto.comment.LikeToCommentCreationDto;
import com.instagram.likeservice.dto.comment.LikeToCommentInformationDto;
import com.instagram.likeservice.dto.post.LikeToPostCreationDto;
import com.instagram.likeservice.dto.post.LikeToPostInformationDto;
import com.instagram.likeservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post")
    public CompletableFuture<ResponseEntity<LikeToPostInformationDto>> likePost(@RequestBody @Valid LikeToPostCreationDto creationDto) {
        return likeService.putLikeToPost(creationDto)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    @PostMapping("/comment")
    public CompletableFuture<ResponseEntity<LikeToCommentInformationDto>> likeComment(@RequestBody @Valid LikeToCommentCreationDto creationDto) {
        return likeService.putLikeToComment(creationDto)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    @DeleteMapping("/delete-post-like/{postId}")
    public CompletableFuture<ResponseEntity<String>> deletePost(
            @RequestHeader String username,
            @PathVariable String postId) throws AccessDeniedException {
        return likeService.unlikePost(postId, username)
                .thenApply(v -> ResponseEntity.ok("✅ Like removed"))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ " + ex.getMessage()));
    }

    @DeleteMapping("/delete-comment-like/{commentId}")
    public CompletableFuture<ResponseEntity<String>> deleteComment(
            @RequestHeader String username,
            @PathVariable String commentId) throws AccessDeniedException {
        return likeService.unlikeComment(commentId, username)
                .thenApply(v -> ResponseEntity.ok("✅ Like removed"))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ " + ex.getMessage()));
    }
}
