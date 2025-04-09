package com.instagram.userdatamanagementservice.mapper;

import com.instagram.userdatamanagementservice.config.SecurityConfig;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.dto.UserResponseDto;
import com.instagram.userdatamanagementservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityMapper {

    private final SecurityConfig securityConfig;

    public User mapToEntity(UserRegistrationDto userRegistrationDto) {
        return User.builder()
                .firstName(userRegistrationDto.firstName())
                .lastName(userRegistrationDto.lastName())
                .username(userRegistrationDto.username())
                .email(userRegistrationDto.email())
                .password(securityConfig.passwordEncoder().encode(userRegistrationDto.password()))
                .sex(userRegistrationDto.sex())
                .build();
    }

    public UserResponseDto mapToResponse(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
