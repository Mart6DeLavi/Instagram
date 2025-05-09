package com.instagram.feedservice.service;

import com.instagram.dto.PostInformationDto;
import com.instagram.feedservice.client.FollowServiceClient;
import com.instagram.feedservice.client.PostServiceClient;
import com.instagram.feedservice.client.UserDataManagementClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowServiceClient followServiceClient;
    private final PostServiceClient postServiceClient;
    private final UserDataManagementClient userDataManagementClient;

    public List<PostInformationDto> getAllPostsFromUsersFollowingByUsername(String username) {
        Long userId = userDataManagementClient.getUserIdByUsername(username);

        if (userId == null) {
            throw new NoSuchElementException("No user with username " + username);
        }

        List<Long> followeeIds = followServiceClient.getFollowings(userId).getBody();

        if (followeeIds == null || followeeIds.isEmpty()) {
            return Collections.emptyList();
        }

        return followeeIds.stream()
                .map(postServiceClient::getAllPostsByUserId)
                .filter(Objects::nonNull)
                .map(ResponseEntity::getBody)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
