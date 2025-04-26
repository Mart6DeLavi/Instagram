package com.instagram.likeservice.mapper;

import com.instagram.likeservice.dto.comment.LikeToCommentInformationDto;
import com.instagram.likeservice.dto.post.LikeToPostCreationDto;
import com.instagram.likeservice.dto.post.LikeToPostInformationDto;
import com.instagram.likeservice.entity.LikeToComment;
import com.instagram.likeservice.entity.LikeToPost;

public class EntityMapper {

    public static LikeToPostInformationDto mapToLikeToPostInformation(LikeToPost likeToPost) {
        return LikeToPostInformationDto.builder()
                .id(likeToPost.getId())
                .userId(likeToPost.getUserId())
                .postId(likeToPost.getPostId())
                .createdAt(likeToPost.getCreatedAt())
                .build();
    }

    public static LikeToCommentInformationDto mapToLikeToCommentInformation(LikeToComment likeToComment) {
        return LikeToCommentInformationDto.builder()
                .id(likeToComment.getId())
                .userId(likeToComment.getUserId())
                .commentId(likeToComment.getCommentId())
                .createdAt(likeToComment.getCreatedAt())
                .build();
    }
}
