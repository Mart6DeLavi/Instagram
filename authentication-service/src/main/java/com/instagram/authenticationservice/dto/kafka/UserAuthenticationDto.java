package com.instagram.authenticationservice.dto.kafka;

import lombok.Builder;

@Builder
public record UserAuthenticationDto(
        String username,
        String password
) {
}
