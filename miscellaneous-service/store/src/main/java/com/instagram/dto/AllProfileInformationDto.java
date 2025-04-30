package com.instagram.dto;

import lombok.Builder;

@Builder
public record AllProfileInformationDto(
        Integer numberOfPosts,
        Integer numberOfSubscribers,
        Integer numberOfSubscriptions,
        String aboutMyself,
        String avatarUrl,
        Boolean isPublic,
        Boolean isVerified,
        Boolean isOnline
) {
}
