package com.instagram.dto.feign;

import lombok.Builder;

@Builder
public record ProfileInformationOfSubscriptionsDto(
        Long userId,
        String username,
        String avatarUrl,
        boolean isVerified,
        boolean isOnline
) {
}
