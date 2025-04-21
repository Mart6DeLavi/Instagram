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
}
