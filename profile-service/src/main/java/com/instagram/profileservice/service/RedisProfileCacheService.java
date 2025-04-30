package com.instagram.profileservice.service;

import com.instagram.profileservice.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisProfileCacheService {

    private final RedisTemplate<String, UserProfile> redisTemplate;
    private static final long EXPIRATION_MINUTES = 15;

    public void cacheProfile(UserProfile profile) {
        String key = getKey(profile.getUsername());
        redisTemplate.opsForValue().set(key, profile, Duration.ofMinutes(EXPIRATION_MINUTES));
    }

    public Optional<UserProfile> getCachedProfile(String username) {
        String key = getKey(username);
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void evictProfileFromCache(String username) {
        redisTemplate.delete(getKey(username));
    }

    private String getKey(String username) {
        return "profile:" + username;
    }
}

