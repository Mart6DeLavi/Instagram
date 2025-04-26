package com.instagram.likeservice.dto.comment;

import lombok.Builder;

@Builder
public record LikeToCommentCreationDto(
        String username,
        String commentId
) {
}
