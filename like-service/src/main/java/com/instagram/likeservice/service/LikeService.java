package com.instagram.likeservice.service;

import com.instagram.dto.kafka.LikeCommentEventDto;
import com.instagram.exception.TokenNotFoundException;
import com.instagram.exception.UserNotFoundException;
import com.instagram.likeservice.client.AuthenticationServiceClient;
import com.instagram.likeservice.client.CommentsServiceClient;
import com.instagram.likeservice.client.PostServiceClient;
import com.instagram.likeservice.client.UserDataManagementClient;
import com.instagram.likeservice.dto.comment.LikeToCommentCreationDto;
import com.instagram.likeservice.dto.comment.LikeToCommentInformationDto;
import com.instagram.dto.kafka.PostEventDto;
import com.instagram.likeservice.dto.post.LikeToPostCreationDto;
import com.instagram.likeservice.dto.post.LikeToPostInformationDto;
import com.instagram.likeservice.entity.LikeToComment;
import com.instagram.likeservice.entity.LikeToPost;
import com.instagram.likeservice.kafka.KafkaProducer;
import com.instagram.likeservice.mapper.EntityMapper;
import com.instagram.likeservice.repository.LikeToCommentRepository;
import com.instagram.likeservice.repository.LikeToPostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeToCommentRepository likeToCommentRepository;
    private final LikeToPostRepository likeToPostRepository;
    private final PostServiceClient postServiceClient;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final UserDataManagementClient userDataManagementClient;
    private final CommentsServiceClient commentsServiceClient;
    private final KafkaProducer kafkaProducer;

    private static final Map<String, Long> userCache = new ConcurrentHashMap<>();

    @Async
    @Transactional
    public CompletableFuture<LikeToPostInformationDto> putLikeToPost(@NonNull @Valid LikeToPostCreationDto creationDto) {
        Long userId = getUserIdByUsername(creationDto.username());
        validateTokenExists(creationDto.username());
        validPostExists(creationDto.postId());

        LikeToPost like = LikeToPost.builder()
                .postId(creationDto.postId())
                .userId(userId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        kafkaProducer.sendLikeToPostEvent(PostEventDto.builder().postId(creationDto.postId()).build());

        LikeToPost saved = likeToPostRepository.save(like);
        return CompletableFuture.completedFuture(EntityMapper.mapToLikeToPostInformation(saved));
    }

    @Async
    @Transactional
    public CompletableFuture<LikeToCommentInformationDto> putLikeToComment(@NonNull @Valid LikeToCommentCreationDto creationDto) {
        Long userId = getUserIdByUsername(creationDto.username());
        validateTokenExists(creationDto.username());
        validCommentExists(creationDto.commentId());

        LikeToComment like = LikeToComment.builder()
                .userId(userId)
                .commentId(creationDto.commentId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        kafkaProducer.sendLikeToCommentEvent(LikeCommentEventDto.builder().commentId(creationDto.commentId()).build());

        LikeToComment saved = likeToCommentRepository.save(like);
        return CompletableFuture.completedFuture(EntityMapper.mapToLikeToCommentInformation(saved));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> unlikePost(String postId, String username) throws AccessDeniedException {
        Long userId = getUserIdByUsername(username);
        validateTokenExists(username);
        validPostExists(postId);

        boolean likeExists = likeToPostRepository.existsByPostIdAndUserId(postId, userId);
        if (!likeExists) {
            throw new AccessDeniedException("You are not the owner of this like or like does not exist");
        }

        likeToPostRepository.deleteLikeToPostByPostIdAndUserId(postId, userId);
        kafkaProducer.sendUnlikeToPostEvent(PostEventDto.builder().postId(postId).build());

        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> unlikeComment(String commentId, String username) throws AccessDeniedException {
        Long userId = getUserIdByUsername(username);
        validateTokenExists(username);
        validCommentExists(commentId);

        boolean likeExists = likeToCommentRepository.existsByCommentIdAndUserId(commentId, userId);
        if (!likeExists) {
            throw new AccessDeniedException("You are not the owner of this like or like does not exist");
        }

        likeToCommentRepository.deleteLikeToCommentByCommentIdAndUserId(commentId, userId);
        kafkaProducer.sendUnlikeToCommentEvent(LikeCommentEventDto.builder().commentId(commentId).build());

        return CompletableFuture.completedFuture(null);
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

    private void validPostExists(String postId) {
        if (!postServiceClient.isPostExist(postId)) {
            throw new NoSuchElementException("Post not found for postId: " + postId);
        }
    }

    private void validCommentExists(String commentId) {
        if (!commentsServiceClient.isCommentExist(commentId)) {
            throw new NoSuchElementException("Comment not found for commentId: " + commentId);
        }
    }
}
