package com.instagram.feedservice.service;

import com.instagram.dto.PostInformationDto;
import com.instagram.feedservice.client.FollowServiceClient;
import com.instagram.feedservice.client.PostServiceClient;
import com.instagram.feedservice.client.UserDataManagementClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowServiceClient followServiceClient;
    private final PostServiceClient postServiceClient;
    private final UserDataManagementClient userDataManagementClient;

    @Async
    public CompletableFuture<List<PostInformationDto>> getAllPostsFromUsersFollowingByUsername(String username) {
        Long userId = userDataManagementClient.getUserIdByUsername(username);
        if (userId == null) {
            throw new NoSuchElementException("No user with username " + username);
        }

        List<Long> followeeIds = followServiceClient.getFollowings(userId).getBody();
        if (followeeIds == null || followeeIds.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<CompletableFuture<List<PostInformationDto>>> futures = followeeIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                    try {
                        ResponseEntity<List<PostInformationDto>> response = postServiceClient.getAllPostsByUserId(id);
                        return response.getBody() != null ? response.getBody() : Collections.<PostInformationDto>emptyList();
                    } catch (Exception e) {
                        log.warn("Failed to fetch posts for userId {}: {}", id, e.getMessage());
                        return Collections.<PostInformationDto>emptyList();
                    }
                }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .flatMap(f -> f.join().stream())
                        .toList());
    }
}
