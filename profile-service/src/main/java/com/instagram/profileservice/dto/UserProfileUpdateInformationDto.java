package com.instagram.profileservice.dto;

public record UserProfileUpdateInformationDto(
        String aboutMyself,
        Boolean isPublic
) {
}
