package com.instagram.userdatamanagementservice.dto.kafka;

import lombok.Builder;

@Builder
public record UserAuthenticationDto(
        String username,
        String password
) {
}
