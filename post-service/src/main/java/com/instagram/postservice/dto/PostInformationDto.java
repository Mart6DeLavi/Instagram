package com.instagram.postservice.dto;

import com.instagram.postservice.document.Location;
import com.instagram.postservice.document.Media;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record PostInformationDto(
        String id,
        List<Media> mediaList,
        String description,
        Location location,
        List<String> tags,
        Integer likesCount,
        Integer commentsCount,
        Date createdAt
) {
}
