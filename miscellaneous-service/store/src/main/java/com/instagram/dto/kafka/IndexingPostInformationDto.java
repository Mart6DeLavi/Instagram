package com.instagram.dto.kafka;

import lombok.Builder;

import java.util.List;

@Builder
public record IndexingPostInformationDto(
    String postId,
    Long userId,
    Location location,
    List<String> tags,
    String description
) {
}
