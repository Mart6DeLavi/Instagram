package com.instagram.profileservice.service;

import com.instagram.exception.TokenNotFoundException;
import com.instagram.exception.UserNotFoundException;
import com.instagram.profileservice.client.AuthenticationServiceClient;
import com.instagram.profileservice.client.UserDataManagementClient;
import com.instagram.profileservice.dto.AllProfileInformationDto;
import com.instagram.profileservice.dto.UserProfileUpdateInformationDto;
import com.instagram.profileservice.entity.UserProfile;
import com.instagram.profileservice.mapper.EntityMapper;
import com.instagram.profileservice.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserDataManagementClient userDataManagementClient;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final S3Service s3Service;

    private static final Map<String, Long> userCache = new ConcurrentHashMap<>();

    public AllProfileInformationDto getAllProfileInformation(@NonNull String username) {
        validateTokenExists(username);
        Long userId = getUserIdByUsername(username);

        UserProfile userProfile = userProfileRepository
                .getUserProfileByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        log.info("Profile found for username {}", username);

        return EntityMapper.mapToProfileInformationDto(userProfile);
    }

    @Transactional
    public UserProfile updateProfileInformation(@NonNull String username, @Valid UserProfileUpdateInformationDto dto) {
        validateTokenExists(username);
        Long userId = getUserIdByUsername(username);

        UserProfile userProfile = userProfileRepository
                .getUserProfileByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        log.info("Profile found for username {}", username);

        if (dto.aboutMyself() != null  && !dto.aboutMyself().isEmpty()) {
            userProfile.setAboutMyself(dto.aboutMyself());
            log.info("Profile about myself update for user: {}", username);
        }

        if (dto.isPublic() != null) {
            userProfile.setIsPublic(dto.isPublic());
            log.info("Profile isPublic update for user: {}", username);
        }

        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public UserProfile createProfile(@NonNull String username, @NonNull String aboutMyself, @NonNull MultipartFile avatar)
    {
        validateTokenExists(username);
        String avatarUrl = s3Service.uploadFile(avatar);
        Long userId = getUserIdByUsername(username);

        UserProfile userProfile = UserProfile.builder()
                .userId(userId)
                .username(username)
                .aboutMyself(aboutMyself)
                .avatarUrl(avatarUrl)
                .isOnline(false)
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public void deleteUserProfile(@NonNull String username) {
        validateTokenExists(username);
        Long userId = getUserIdByUsername(username);

        UserProfile userProfile = userProfileRepository
                .getUserProfileByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        userProfileRepository.delete(userProfile);
        userCache.remove(username);
    }

    private void validateTokenExists(String username) {
        String token = authenticationServiceClient.findTokenByUsername(username);
        log.info("Token found for user: {}", username);
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException("Token not found for username: " + username);
        }
    }

    private Long getUserIdByUsername(String username) {
        return userCache.computeIfAbsent(username, key -> {
            log.info("Cache miss: requesting userId for username '{}'", key);

            Long id = userDataManagementClient.getUserIdByUsername(key);

            if (id == null) {
                log.warn("User with username '{}' not found in userDataManagementClient", key);
                throw new UserNotFoundException("User not found for username: " + key);
            }

            log.info("Retrieved and cached userId '{}' for username '{}'", id, key);
            return id;
        });
    }
}
