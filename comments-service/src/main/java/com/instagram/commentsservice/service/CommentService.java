package com.instagram.commentsservice.service;

import com.instagram.commentsservice.client.AuthenticationServiceClient;
import com.instagram.commentsservice.client.UserDataManagementClient;
import com.instagram.commentsservice.dto.CommentCreationDto;
import com.instagram.commentsservice.dto.CommentInformationDto;
import com.instagram.commentsservice.dto.CommentUpdateInformationDto;
import com.instagram.commentsservice.entity.Comment;
import com.instagram.commentsservice.kafka.KafkaProducer;
import com.instagram.commentsservice.mapper.EntityMapper;
import com.instagram.commentsservice.repository.CommentRepository;
import com.instagram.exception.TokenNotFoundException;
import com.instagram.exception.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final UserDataManagementClient userDataManagementClient;
    private final KafkaProducer kafkaProducer;

    private static final Map<String, Long> userCache = new ConcurrentHashMap<>();

    public boolean isCommentExist(String commentId) {
        ObjectId commentObjectId = new ObjectId(commentId);
        return commentRepository.getCommentByCommentId(commentObjectId).isPresent();
    }

    @Async
    public CompletableFuture<List<CommentInformationDto>> getAllCommentsByPostId(String username, String postId) {
        validateTokenExists(username);

        List<CommentInformationDto> result = commentRepository.getAllCommentsByPostId(postId).stream()
                .map(comment -> {
                    String actualUsername = userDataManagementClient.getUsernameByUserId(comment.getUserId());

                    if (actualUsername != null && !actualUsername.equals(comment.getUsername())) {
                        comment.setUsername(actualUsername);
                        commentRepository.save(comment);
                    }

                    return EntityMapper.mapToCommentInformationDto(comment);
                })
                .sorted(Comparator.comparing(CommentInformationDto::likesCount).reversed())
                .toList();

        return CompletableFuture.completedFuture(result);
    }

    @Async
    public CompletableFuture<CommentInformationDto> createNewComment(CommentCreationDto creationDto) {
        Long userId = getUserIdByUsername(creationDto.username());
        validateTokenExists(creationDto.username());

        Comment comment = Comment.builder()
                .userId(userId)
                .postId(creationDto.postId())
                .username(creationDto.username())
                .commentText(creationDto.commentText())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        kafkaProducer.sendCommentCreatedEvent(creationDto.postId());

        return CompletableFuture.completedFuture(
                EntityMapper.mapToCommentInformationDto(commentRepository.save(comment))
        );
    }

    @Async
    public CompletableFuture<CommentInformationDto> updateCommentInformation(
            String postId, String commentId, CommentUpdateInformationDto updateDto) {
        validateTokenExists(updateDto.username());

        return commentRepository.getAllCommentsByPostId(postId).stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .map(comment -> {
                    if (updateDto.commentText() != null && !updateDto.commentText().equals(comment.getCommentText())) {
                        comment.setCommentText(updateDto.commentText());
                        commentRepository.save(comment);
                    }
                    return CompletableFuture.completedFuture(EntityMapper.mapToCommentInformationDto(comment));
                })
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
    }

    @Async
    public CompletableFuture<Void> deleteComment(String username, String postId, String commentId) {
        validateTokenExists(username);
        Long userId = getUserIdByUsername(username);

        commentRepository.getAllCommentsByPostId(postId).stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .ifPresentOrElse(
                        comment -> {
                            if (!comment.getUserId().equals(userId)) {
                                throw new SecurityException("You are not the author of this comment");
                            }
                            commentRepository.delete(comment);
                        },
                        () -> { throw new NoSuchElementException("Comment not found"); }
                );

        kafkaProducer.sendCommentDeletedEvent(postId);
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
            if (id == null) {
                throw new UserNotFoundException("User not found for username: " + key);
            }
            return id;
        });
    }
}
