package com.instagram.dto.kafka;

import lombok.Builder;

@Builder
public record LikeCommentEventDto(String commentId) {
}
