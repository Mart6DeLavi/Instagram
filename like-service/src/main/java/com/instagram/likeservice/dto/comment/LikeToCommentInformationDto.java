package com.instagram.likeservice.dto.comment;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record LikeToCommentInformationDto(
        Long id,
        Long userId,
        String commentId,
        Timestamp createdAt
) {
}
