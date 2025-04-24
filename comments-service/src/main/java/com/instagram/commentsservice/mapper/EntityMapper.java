package com.instagram.commentsservice.mapper;

import com.instagram.commentsservice.entity.Comment;
import com.instagram.commentsservice.dto.CommentInformationDto;

public class EntityMapper {
    public static CommentInformationDto mapToCommentInformationDto(Comment comment) {
        return CommentInformationDto.builder()
                .commentId(comment.getId())
                .username(comment.getUsername())
                .commentText(comment.getCommentText())
                .likesCount(comment.getLikesCount())
                .isEdited(comment.isEdited())
                .build();
    }
}
