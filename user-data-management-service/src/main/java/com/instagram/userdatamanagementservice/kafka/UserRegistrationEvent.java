package com.instagram.userdatamanagementservice.kafka;

import com.instagram.dto.kafka.UserRegistrationDto;

public record UserRegistrationEvent(
        UserRegistrationDto dto) {
}