package com.instagram.profileservice.mapper;

import com.instagram.profileservice.dto.AllProfileInformationDto;
import com.instagram.profileservice.entity.UserProfile;

public class EntityMapper {

    public static AllProfileInformationDto mapToProfileInformationDto(UserProfile userProfile) {
        return AllProfileInformationDto.builder()
                .avatarUrl(userProfile.getAvatarUrl())
                .numberOfPosts(userProfile.getNumberOfPosts())
                .numberOfSubscribers(userProfile.getNumberOfSubscribers())
                .numberOfSubscriptions(userProfile.getNumberOfSubscriptions())
                .aboutMyself(userProfile.getAboutMyself())
                .isPublic(userProfile.getIsPublic())
                .isVerified(userProfile.getIsVerified())
                .isOnline(userProfile.getIsOnline())
                .build();
    }
}
