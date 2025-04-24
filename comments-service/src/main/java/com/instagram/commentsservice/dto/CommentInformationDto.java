package com.instagram.commentsservice.dto;

import lombok.Builder;

@Builder
public record CommentInformationDto(
        String commentId,
        String username,
        String commentText,
        int likesCount,
        boolean isEdited
) {
}
