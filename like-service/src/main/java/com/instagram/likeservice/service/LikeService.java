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
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.NoSuchElementException;
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

    @Transactional
    public LikeToPostInformationDto putLikeToPost(@NonNull @Valid LikeToPostCreationDto creationDto) {
        Long userId = getUserIdByUsername(creationDto.username());
        validateTokenExists(creationDto.username());
        validPostExists(creationDto.postId());

        LikeToPost like = LikeToPost.builder()
                .postId(creationDto.postId())
                .userId(userId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        PostEventDto likeToPost = PostEventDto.builder()
                .postId(creationDto.postId())
                .build();

        kafkaProducer.sendLikeToPostEvent(likeToPost);

        return EntityMapper.mapToLikeToPostInformation(likeToPostRepository.save(like));
    }

    @Transactional
    public LikeToCommentInformationDto putLikeToComment(@NonNull @Valid LikeToCommentCreationDto creationDto) {
        Long userId = getUserIdByUsername(creationDto.username());
        validateTokenExists(creationDto.username());
        validCommentExists(creationDto.commentId());

        LikeToComment like = LikeToComment.builder()
                .userId(userId)
                .commentId(creationDto.commentId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        LikeCommentEventDto likeToComment = LikeCommentEventDto.builder()
                .commentId(creationDto.commentId())
                .build();

        kafkaProducer.sendLikeToCommentEvent(likeToComment);
        log.info("send");
        return EntityMapper.mapToLikeToCommentInformation(likeToCommentRepository.save(like));
    }

    @Transactional
    public void unlikePost(String postId, String username) throws AccessDeniedException {
        Long userId = getUserIdByUsername(username);
        validateTokenExists(username);
        validPostExists(postId);

        boolean likeExists = likeToPostRepository.existsByPostIdAndUserId(postId, userId);
        if (!likeExists) {
            log.warn("User '{}' tried to remove like from post '{}', but it doesn't belong to them", username, postId);
            throw new AccessDeniedException("You are not the owner of this like or like does not exist");
        }

        likeToPostRepository.deleteLikeToPostByPostIdAndUserId(postId, userId);

        PostEventDto postEventDto = PostEventDto.builder()
                .postId(postId)
                .build();

        kafkaProducer.sendUnlikeToPostEvent(postEventDto);

        log.info("Like deleted successfully on post by user '{}'", username);
    }


    @Transactional
    public void unlikeComment(String commentId, String username) throws AccessDeniedException {
        Long userId = getUserIdByUsername(username);
        validateTokenExists(username);
        validCommentExists(commentId);

        boolean likeExists = likeToCommentRepository.existsByCommentIdAndUserId(commentId, userId);
        if (!likeExists) {
            log.warn("User '{}' tried to remove like from comment '{}', but it doesn't belong to them", username, commentId);
            throw new AccessDeniedException("You are not the owner of this like or like does not exist");
        }

        likeToCommentRepository.deleteLikeToCommentByCommentIdAndUserId(commentId, userId);

        LikeCommentEventDto commentEventDto = LikeCommentEventDto.builder()
                .commentId(commentId)
                .build();

        kafkaProducer.sendUnlikeToCommentEvent(commentEventDto);

        log.info("Like deleted successfully on comment by user '{}'", username);
    }


    private void validateTokenExists(String username) {
        String token = authenticationServiceClient.findTokenByUsername(username);
        log.info("Token found for user: {}", username);
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException("Token not found for username: " + username);
        }
    }

    private Long getUserIdByUsername(String username) {
        return userCache.computeIfAbsent(username, key -> {
            log.info("Cache miss: requesting userId for username '{}'", key);

            Long id = userDataManagementClient.getUserIdByUsername(key);

            if (id == null) {
                log.warn("User with username '{}' not found in userDataManagementClient", key);
                throw new UserNotFoundException("User not found for username: " + key);
            }

            log.info("Retrieved and cached userId '{}' for username '{}'", id, key);
            return id;
        });
    }

    private void validPostExists(String postId) {
        var post = postServiceClient.isPostExist(postId);
        log.info("Post found for postId: {}", postId);

        if (!post) {
            throw new NoSuchElementException("Post not found for postId: " + postId);
        }
    }

    private void validCommentExists(String commentId) {
        var comment = commentsServiceClient.isCommentExist(commentId);
        log.info("Comment found for commentId: {}", commentId);

        if (!comment) {
            throw new NoSuchElementException("Comment not found for commentId: " + commentId);
        }
    }
}
