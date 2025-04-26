package com.instagram.likeservice.dto.post;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record LikeToPostInformationDto(
        Long id,
        Long userId,
        String postId,
        Timestamp createdAt
) {
}
