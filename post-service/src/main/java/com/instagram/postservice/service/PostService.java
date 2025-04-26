package com.instagram.postservice.service;

import com.instagram.exception.TokenNotFoundException;
import com.instagram.exception.UserNotFoundException;
import com.instagram.postservice.client.AuthenticationServiceClient;
import com.instagram.postservice.client.UserDataManagementClient;
import com.instagram.postservice.document.Location;
import com.instagram.postservice.document.Media;
import com.instagram.postservice.document.Post;
import com.instagram.postservice.dto.PostInformationDto;
import com.instagram.postservice.kafka.KafkaProducer;
import com.instagram.postservice.mapper.EntityMapper;
import com.instagram.postservice.repository.PostRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final UserDataManagementClient userDataManagementClient;
    private final S3Service s3Service;
    private final KafkaProducer kafkaProducer;

    private static final Map<String, Long> userCache = new ConcurrentHashMap<>();

    public List<PostInformationDto> getAllPostsByUsername(@NonNull String username) {
        Long userId = getUserIdByUsername(username);
        validateTokenExists(username);

        List<Post> posts = postRepository.findAllByUserId(userId);

        return posts.stream()
                .map(EntityMapper::toPostInformationDto)
                .toList();
    }

    public boolean isPostExist(@NonNull String postId) {
        ObjectId postObjectId = new ObjectId(postId);

        var post = postRepository.getPostByPostId(postObjectId);

        return post.isPresent();
    }

    public PostInformationDto createNewPost(String username,
                                            String description,
                                            List<String> tags,
                                            double latitude,
                                            double longitude,
                                            String locationName,
                                            List<MultipartFile> files) {

        Long userId = getUserIdByUsername(username);
        validateTokenExists(username);

        Location location = Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .placeName(locationName)
                .build();

        List<Media> mediaList = files.stream()
                .map(file -> Media.builder()
                        .url(s3Service.uploadFile(file))
                        .type(resolveType(file))
                        .build())
                .toList();

        Post post = Post.builder()
                .userId(userId)
                .description(description)
                .tags(tags)
                .location(location)
                .mediaList(mediaList)
                .createdAt(new Date())
                .likesCount(0)
                .commentsCount(0)
                .build();

        kafkaProducer.sendPostCreatedEvent(userId);

        log.info("message sent to kafka");

        return EntityMapper.toPostInformationDto(postRepository.save(post));
    }

    private String resolveType(MultipartFile file) {
        if (file.getContentType().startsWith("image")) { return "image"; }
        if (file.getContentType().startsWith("video")) { return "video"; }
        return "unknown";
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
}
