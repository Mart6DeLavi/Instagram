package com.instagram.postservice.kafka;

import com.instagram.dto.kafka.CommentEventDto;
import com.instagram.dto.kafka.LikeCommentEventDto;
import com.instagram.dto.kafka.PostEventDto;
import com.instagram.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final PostRepository postRepository;

    @KafkaListener(
            topics = "comment-created-event",
            groupId = "post-service",
            containerFactory = "kafkaListenerCommentEventContainerFactory"
    )
    public void handleNewComment(CommentEventDto event) {
        log.info("Received created event for comment = {}", event.postId());

        ObjectId postObjectId = new ObjectId(event.postId());

        postRepository.getPostByPostId(postObjectId)
                .ifPresent(post -> {
                    post.setCommentsCount(post.getCommentsCount() + 1);
                    postRepository.save(post);
                    log.info("Updated post count for commentId = {}", event.postId());
                });
    }

    @KafkaListener(
            topics = "comment-deleted-event",
            groupId = "post-service",
            containerFactory = "kafkaListenerCommentEventContainerFactory"
    )
    public void handleDeletedCommented(CommentEventDto event) {
        log.info("Received deleted event for comment = {}", event.postId());

        ObjectId postObjectId = new ObjectId(event.postId());

        postRepository.getPostByPostId(postObjectId)
                .ifPresent(post -> {
                    post.setCommentsCount(post.getCommentsCount() - 1);
                    postRepository.save(post);
                    log.info("Updated post count for commentId = {}", event.postId());
                });
    }

    @KafkaListener(
            topics = "like-to-post-event",
            groupId = "post-service",
            containerFactory = "kafkaListenerPostEventContainerFactory"
    )
    public void handleNewLike(PostEventDto event) {
        log.info("Received liked event for post = {}", event.postId());

        ObjectId postObjectId = new ObjectId(event.postId());

        postRepository.getPostByPostId(postObjectId)
                .ifPresent(post -> {
                    post.setLikesCount(post.getLikesCount() + 1);
                    postRepository.save(post);
                    log.info("Updated post count for likes = {}", event.postId());
                });
    }

    @KafkaListener(
            topics = "unlike-to-post-event",
            groupId = "post-service",
            containerFactory = "kafkaListenerPostEventContainerFactory"
    )
    public void handleUnlike(PostEventDto event) {
        log.info("Received unliked event for post = {}", event.postId());

        ObjectId postObjectId = new ObjectId(event.postId());

        postRepository.getPostByPostId(postObjectId)
                .ifPresent(post -> {
                    post.setLikesCount(post.getLikesCount() - 1);
                    postRepository.save(post);
                    log.info("Updated post count for likes = {}", event.postId());
                });
    }
}
