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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post")
    public ResponseEntity<LikeToPostInformationDto> likePost(@RequestBody @Valid LikeToPostCreationDto creationDto) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.putLikeToPost(creationDto));
    }

    @PostMapping("/comment")
    public ResponseEntity<LikeToCommentInformationDto> likeComment(@RequestBody @Valid LikeToCommentCreationDto creationDto) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.putLikeToComment(creationDto));
    }

    @DeleteMapping("/delete-post-like/{postId}")
    public ResponseEntity<?> deletePost(
            @RequestHeader String username,
            @PathVariable String postId) {
        try {
            likeService.unlikePost(postId, username);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-comment-like/{commentId}")
    public ResponseEntity<?> deleteComment(
            @RequestHeader String username,
            @PathVariable String commentId
    ) {
        try {
            likeService.unlikeComment(commentId, username);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
