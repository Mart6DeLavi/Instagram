package com.instagram.likeservice.kafka;

import com.instagram.dto.kafka.LikeCommentEventDto;
import com.instagram.dto.kafka.PostEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, PostEventDto> kafkaPostTemplate;
    private final KafkaTemplate<String, LikeCommentEventDto> kafkaCommentTemplate;

    private static final String POST_LIKE_TOPIC = "like-to-post-event";
    private static final String POST_UNLIKE_TOPIC = "unlike-to-post-event";

    private static final String COMMENT_LIKE_TOPIC = "like-to-comment-event";
    private static final String COMMENT_UNLIKE_TOPIC = "unlike-to-comment-event";


    public void sendLikeToPostEvent(PostEventDto postEventDto) {
        kafkaPostTemplate.send(POST_LIKE_TOPIC, postEventDto);
    }

    public void sendLikeToCommentEvent(LikeCommentEventDto commentEventDto) {
        kafkaCommentTemplate.send(COMMENT_LIKE_TOPIC, commentEventDto);
    }

    public void sendUnlikeToCommentEvent(LikeCommentEventDto commentEventDto) {
        kafkaCommentTemplate.send(COMMENT_UNLIKE_TOPIC, commentEventDto);
    }

    public void sendUnlikeToPostEvent(PostEventDto postEventDto) {
        kafkaPostTemplate.send(POST_UNLIKE_TOPIC, postEventDto);
    }
}
