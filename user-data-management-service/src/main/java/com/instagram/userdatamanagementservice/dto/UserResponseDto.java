package com.instagram.userdatamanagementservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponseDto(
        String username,
        LocalDateTime createdAt
) {
}
