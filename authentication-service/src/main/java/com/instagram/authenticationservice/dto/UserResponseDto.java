package com.instagram.authenticationservice.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        String username,
        String email
) {
}
