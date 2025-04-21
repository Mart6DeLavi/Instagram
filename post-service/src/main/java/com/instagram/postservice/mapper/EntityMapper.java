package com.instagram.postservice.mapper;

import com.instagram.postservice.document.Post;
import com.instagram.postservice.dto.PostInformationDto;

public class EntityMapper {

    public static PostInformationDto toPostInformationDto(Post post) {
        return PostInformationDto.builder()
                .id(post.getId())
                .mediaList(post.getMediaList())
                .description(post.getDescription())
                .location(post.getLocation())
                .tags(post.getTags())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
