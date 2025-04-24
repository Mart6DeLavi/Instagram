package com.instagram.commentsservice.dto;

public record CommentCreationDto(
        String postId,
        String username,
        String commentText
) {
}

