package com.instagram.profileservice.mapper;

import com.instagram.dto.feign.ProfileInformationOfSubscriptionsDto;
import com.instagram.dto.AllProfileInformationDto;
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

    public static ProfileInformationOfSubscriptionsDto mapToProfileInformationOfSubscriptionsDto(UserProfile userProfile) {
        return ProfileInformationOfSubscriptionsDto.builder()
                .userId(userProfile.getUserId())
                .username(userProfile.getUsername())
                .avatarUrl(userProfile.getAvatarUrl())
                .isVerified(userProfile.getIsVerified())
                .isOnline(userProfile.getIsOnline())
                .build();
    }
}
