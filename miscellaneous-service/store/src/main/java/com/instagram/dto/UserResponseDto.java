package com.instagram.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponseDto(
        String username,
        String email,
        LocalDateTime createdAt
) {
}
