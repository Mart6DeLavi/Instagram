package com.instagram.dto.kafka;

import com.instagram.model.Sex;
import lombok.Builder;

@Builder
public record UserRegistrationDto(
        String firstName,
        String lastName,
        String username,
        String email,
        String password,
        Sex sex
) {
}
