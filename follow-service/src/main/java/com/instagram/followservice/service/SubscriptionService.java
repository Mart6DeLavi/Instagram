package com.instagram.followservice.service;

import com.instagram.exception.TokenNotFoundException;
import com.instagram.exception.UserNotFoundException;
import com.instagram.followservice.client.AuthenticationServiceClient;
import com.instagram.followservice.client.ProfileServiceClient;
import com.instagram.followservice.client.UserDataManagementClient;
import com.instagram.followservice.entity.Subscription;
import com.instagram.followservice.kafka.KafkaProducer;
import com.instagram.followservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final UserDataManagementClient userDataManagementClient;
    private final ProfileServiceClient profileServiceClient;

    private static final Map<String, Long> userCache = new ConcurrentHashMap<>();
    private final KafkaProducer kafkaProducer;

    @Transactional
    public void makeFollower(String followerUsername, String followeeUsername) {
        Long followerId = getUserIdByUsername(followerUsername);
        Long followeeId = getUserIdByUsername(followeeUsername);
        validateTokenExists(followerUsername);

        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("User cannot follow themselves.");
        }

        if (subscriptionRepository.countByFollowerAndFollowing(followerId, followeeId) > 0) {
            log.info("User {} already follows {}", followerUsername, followeeUsername);
            throw new IllegalArgumentException(String.format("User %s already follows %s", followerUsername, followeeUsername));
        }

        Subscription subscription = Subscription.builder()
                .followerId(followerId)
                .followingId(followeeId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        subscriptionRepository.save(subscription);
        kafkaProducer.handleNewFollower(followeeId);
        kafkaProducer.handleNewFollowing(followerId);
    }

    @Transactional
    public void deleteFollowing(String followerUsername, String followeeUsername) {
        Long followerId = getUserIdByUsername(followerUsername);
        Long followeeId = getUserIdByUsername(followeeUsername);
        validateTokenExists(followerUsername);

        if (subscriptionRepository.countByFollowerAndFollowing(followerId, followeeId) == 0) {
            throw new IllegalArgumentException(String.format("User %d don't follow user %d", followerId, followeeId));
        }

        subscriptionRepository.deleteByFollowerIdAndFollowingId(followerId, followeeId);

        kafkaProducer.handleRemoveFollower(followeeId);
        kafkaProducer.handleRemoveFollowing(followerId);
    }

    @Transactional
    public List<Long> getAllFollowerIdsByUserId(Long userId) {
        return subscriptionRepository.getAllFollowerIdsByUserId(userId);
    }

    @Transactional
    public List<Long> getAllFollowingIdsByUserId(Long userId) {
        return subscriptionRepository.getAllFollowingIdsByUserId(userId);
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
