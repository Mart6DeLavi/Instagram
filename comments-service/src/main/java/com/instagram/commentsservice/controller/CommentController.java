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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/all/{postId}")
    public ResponseEntity<List<CommentInformationDto>> getAllCommentsByPostId(
            @RequestHeader String username,
            @PathVariable String postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByPostId(username, postId));
    }

    @PostMapping("/create")
    public ResponseEntity<CommentInformationDto> createNewComment(@RequestBody CommentCreationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createNewComment(dto));
    }


    @PatchMapping("/update/{postId}/{commentId}")
    public ResponseEntity<CommentInformationDto> updateCommentInformation(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestBody CommentUpdateInformationDto updateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateCommentInformation(postId, commentId, updateDto));
    }

    @DeleteMapping("/delete/{postId}/{commentId}")
    public String deleteComment(
            @RequestHeader String username,
            @PathVariable String postId,
            @PathVariable String commentId
    ) {
        try {
            commentService.deleteComment(username, postId, commentId);
            return String.format("Status: %s", HttpStatus.OK);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
