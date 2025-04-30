package com.instagram.followservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    private static final String NEW_FOLLOWER_TOPIC = "follower-event";
    private static final String NEW_FOLLOWING_TOPIC = "following-event";

    private static final String REMOVE_FOLLOWER_TOPIC = "follower-remove-event";
    private static final String REMOVE_FOLLOWING_TOPIC = "following-remove-event";

    public void handleNewFollower(Long followingId) {
        kafkaTemplate.send(NEW_FOLLOWER_TOPIC, followingId);
    }

    public void handleNewFollowing(Long followerId) {
        kafkaTemplate.send(NEW_FOLLOWING_TOPIC, followerId);
    }

    public void handleRemoveFollower(Long followerId) {
        kafkaTemplate.send(REMOVE_FOLLOWER_TOPIC, followerId);
    }

    public void handleRemoveFollowing(Long followingId) {
        kafkaTemplate.send(REMOVE_FOLLOWING_TOPIC, followingId);
    }
}
