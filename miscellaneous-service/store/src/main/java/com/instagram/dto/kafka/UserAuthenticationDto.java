package com.instagram.dto.kafka;

import lombok.Builder;

@Builder
public record UserAuthenticationDto(
        String username,
        String password
) {
}
