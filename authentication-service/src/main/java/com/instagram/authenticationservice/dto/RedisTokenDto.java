package com.instagram.authenticationservice.dto;

import lombok.Builder;

@Builder
public record RedisTokenDto (
     String username,
     String token
) {
}
