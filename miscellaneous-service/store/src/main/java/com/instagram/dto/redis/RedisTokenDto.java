package com.instagram.dto.redis;

import lombok.Builder;

@Builder
public record RedisTokenDto(
        String token,
        String username
) {
}
