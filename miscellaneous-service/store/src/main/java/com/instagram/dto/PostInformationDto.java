package com.instagram.dto;

import com.instagram.dto.kafka.Location;
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
