package com.instagram.commentsservice.controller;

import com.instagram.commentsservice.dto.CommentCreationDto;
import com.instagram.commentsservice.dto.CommentInformationDto;
import com.instagram.commentsservice.dto.CommentUpdateInformationDto;
import com.instagram.commentsservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public boolean isCommentExist(@PathVariable String commentId) {
        return commentService.isCommentExist(commentId);
    }

    @GetMapping("/all/{postId}")
    public CompletableFuture<ResponseEntity<List<CommentInformationDto>>> getAllCommentsByPostId(
            @RequestHeader String username,
            @PathVariable String postId
    ) {
        return commentService.getAllCommentsByPostId(username, postId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body(List.of()));
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<CommentInformationDto>> createNewComment(@RequestBody CommentCreationDto dto) {
        return commentService.createNewComment(dto)
                .thenApply(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment));
    }

    @PatchMapping("/update/{postId}/{commentId}")
    public CompletableFuture<ResponseEntity<CommentInformationDto>> updateCommentInformation(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestBody CommentUpdateInformationDto updateDto) {
        return commentService.updateCommentInformation(postId, commentId, updateDto)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/delete/{postId}/{commentId}")
    public CompletableFuture<ResponseEntity<String>> deleteComment(
            @RequestHeader String username,
            @PathVariable String postId,
            @PathVariable String commentId
    ) {
        return commentService.deleteComment(username, postId, commentId)
                .thenApply(v -> ResponseEntity.ok("✅ Deleted"))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ " + ex.getMessage()));
    }
}
