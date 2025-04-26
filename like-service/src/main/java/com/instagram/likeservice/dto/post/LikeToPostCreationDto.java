package com.instagram.likeservice.dto.post;

import lombok.Builder;

@Builder
public record LikeToPostCreationDto(
    String username,
    String postId
) {
}
