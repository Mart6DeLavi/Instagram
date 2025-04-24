package com.instagram.commentsservice.kafka;

import com.instagram.dto.kafka.CommentEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, CommentEventDto> kafkaTemplate;

    private static final String TOPIC_CREATED = "comment-created-event";
    private static final String TOPIC_DELETED = "comment-deleted-event";

    public void sendCommentCreatedEvent(String postId) {
        CommentEventDto event = new CommentEventDto(postId);
        kafkaTemplate.send(TOPIC_CREATED, event);
    }

    public void sendCommentDeletedEvent(String postId) {
        CommentEventDto event = new CommentEventDto(postId);
        kafkaTemplate.send(TOPIC_DELETED, event);
    }
}
