package com.instagram.profileservice.kafka;

import com.instagram.dto.kafka.PostCreatedEventDto;
import com.instagram.profileservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final UserProfileRepository userProfileRepository;

    @KafkaListener(topics = "post-events", groupId = "profile-service")
    public void handleNewPost(PostCreatedEventDto event) {
        log.info("Received PostCreatedEvent for userId={}", event.userId());

        userProfileRepository.getUserProfileByUserId(event.userId())
                .ifPresent(profile -> {
                    profile.setNumberOfPosts(profile.getNumberOfPosts() + 1);
                    userProfileRepository.save(profile);
                    log.info("Updated post count for userId={} to {}", event.userId(), profile.getNumberOfPosts());
                });
    }

    @Component
    @RequiredArgsConstructor
    public static class FollowsConsumer {

        private final UserProfileRepository userProfileRepository;

        @KafkaListener(topics = "follower-event", groupId = "profile-service", containerFactory = "followsKafkaListenerContainerFactory")
        public void incrementFollowers(Long userId) {
            userProfileRepository.getUserProfileByUserId(userId)
                    .ifPresent(profile -> {
                        profile.setNumberOfSubscribers(profile.getNumberOfSubscribers() + 1);
                        userProfileRepository.save(profile);
                        log.info("Increased followers count for userId={} to {}", userId, profile.getNumberOfSubscribers());
                    });
        }

        @KafkaListener(topics = "following-event", groupId = "profile-service", containerFactory = "followsKafkaListenerContainerFactory")
        public void incrementFollowings(Long userId) {
            userProfileRepository.getUserProfileByUserId(userId)
                    .ifPresent(profile -> {
                        profile.setNumberOfSubscriptions(profile.getNumberOfSubscriptions() + 1);
                        userProfileRepository.save(profile);
                        log.info("Increased following count for userId={} to {}", userId, profile.getNumberOfSubscriptions());
                    });
        }

        @KafkaListener(topics = "follower-remove-event", groupId = "profile-service", containerFactory = "followsKafkaListenerContainerFactory")
        public void decrementFollowers(Long userId) {
            userProfileRepository.getUserProfileByUserId(userId)
                    .ifPresent(profile -> {
                        profile.setNumberOfSubscribers(Math.max(profile.getNumberOfSubscribers() - 1, 0));
                        userProfileRepository.save(profile);
                        log.info("Decreased followers count for userId={} to {}", userId, profile.getNumberOfSubscribers());
                    });
        }

        @KafkaListener(topics = "following-remove-event", groupId = "profile-service", containerFactory = "followsKafkaListenerContainerFactory")
        public void decrementFollowing(Long userId) {
            userProfileRepository.getUserProfileByUserId(userId)
                    .ifPresent(profile -> {
                        profile.setNumberOfSubscriptions(Math.max(profile.getNumberOfSubscriptions() - 1, 0));
                        userProfileRepository.save(profile);
                        log.info("Decreased following count for userId={} to {}", userId, profile.getNumberOfSubscriptions());
                    });
        }

    }
}

