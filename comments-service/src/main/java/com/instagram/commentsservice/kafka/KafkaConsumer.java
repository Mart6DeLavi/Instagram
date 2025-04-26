package com.instagram.commentsservice.kafka;

import com.instagram.commentsservice.repository.CommentRepository;
import com.instagram.dto.kafka.LikeCommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CommentRepository commentRepository;

    @KafkaListener(topics = "like-to-comment-event", groupId = "comments-service", containerFactory = "kafkaListenerContainerFactory")
    public void handleNewLike(LikeCommentEventDto event) {
        log.info("Received like event to comment = {}", event.commentId());

        ObjectId commentObjectId = new ObjectId(event.commentId());

        commentRepository.getCommentByCommentId(commentObjectId)
                .ifPresent(comment -> {
                    comment.setLikesCount(comment.getLikesCount() + 1);
                    commentRepository.save(comment);
                    log.info("Like count on comment {} changed", event.commentId());
                });
    }

    @KafkaListener(topics = "unlike-to-comment-event", groupId = "comments-service", containerFactory = "kafkaListenerContainerFactory")
    public void handleUnlike(LikeCommentEventDto event) {
        log.info("Received unlike event to comment = {}", event.commentId());

        ObjectId commentObjectId = new ObjectId(event.commentId());

        commentRepository.getCommentByCommentId(commentObjectId)
                .ifPresent(comment -> {
                    comment.setLikesCount(comment.getLikesCount() - 1);
                    commentRepository.save(comment);
                    log.info("Unlike count on comment {} changed", event.commentId());
                });
    }


}
