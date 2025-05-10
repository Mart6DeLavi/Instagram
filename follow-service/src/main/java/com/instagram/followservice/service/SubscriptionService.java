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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final UserDataManagementClient userDataManagementClient;
    private final ProfileServiceClient profileServiceClient;
    private final KafkaProducer kafkaProducer;

    private static final Map<String, Long> userCache = new ConcurrentHashMap<>();

    @Async
    @Transactional
    public CompletableFuture<Void> makeFollower(String followerUsername, String followeeUsername) {
        Long followerId = getUserIdByUsername(followerUsername);
        Long followeeId = getUserIdByUsername(followeeUsername);
        validateTokenExists(followerUsername);

        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("User cannot follow themselves.");
        }

        if (subscriptionRepository.countByFollowerAndFollowing(followerId, followeeId) > 0) {
            log.info("User {} already follows {}", followerUsername, followeeUsername);
            throw new IllegalArgumentException(
                    String.format("User %s already follows %s", followerUsername, followeeUsername));
        }

        subscriptionRepository.save(Subscription.builder()
                .followerId(followerId)
                .followingId(followeeId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build());

        kafkaProducer.handleNewFollower(followeeId);
        kafkaProducer.handleNewFollowing(followerId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> deleteFollowing(String followerUsername, String followeeUsername) {
        Long followerId = getUserIdByUsername(followerUsername);
        Long followeeId = getUserIdByUsername(followeeUsername);
        validateTokenExists(followerUsername);

        if (subscriptionRepository.countByFollowerAndFollowing(followerId, followeeId) == 0) {
            throw new IllegalArgumentException(
                    String.format("User %d doesn't follow user %d", followerId, followeeId));
        }

        subscriptionRepository.deleteByFollowerIdAndFollowingId(followerId, followeeId);
        kafkaProducer.handleRemoveFollower(followeeId);
        kafkaProducer.handleRemoveFollowing(followerId);
        return CompletableFuture.completedFuture(null);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllFollowerIdsByUserId(Long userId) {
        return subscriptionRepository.getAllFollowerIdsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllFollowingIdsByUserId(Long userId) {
        return subscriptionRepository.getAllFollowingIdsByUserId(userId);
    }

    private void validateTokenExists(String username) {
        String token = authenticationServiceClient.findTokenByUsername(username);
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException("Token not found for username: " + username);
        }
    }

    private Long getUserIdByUsername(String username) {
        return userCache.computeIfAbsent(username, key -> {
            Long id = userDataManagementClient.getUserIdByUsername(key);
            if (id == null) throw new UserNotFoundException("User not found for username: " + key);
            return id;
        });
    }
}
