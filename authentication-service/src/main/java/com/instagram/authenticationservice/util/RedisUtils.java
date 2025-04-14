package com.instagram.authenticationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.authenticationservice.exception.TokenNotFoundException;
import com.instagram.dto.redis.RedisTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, RedisTokenDto> redisTemplate;

    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void saveTokenToRedis(String username, String token) {
        String RedisKey = "user: " + username;
        RedisTokenDto redisTokenDto = new RedisTokenDto(token, username);

        try {
            redisTemplate.opsForValue().set(RedisKey, redisTokenDto, TOKEN_TTL);
            log.info("✅ Token stored in Redis for user: {} with TTL: {}", username, TOKEN_TTL);
        } catch (DataAccessException ex) {
            log.error("❌ Unable to store token in Redis for user: {}", username, ex);
        }
    }

    public String findTokenByUsername(String username) {
        String redisKey = "user: " + username;
        Object value = redisTemplate.opsForValue().get(redisKey);

        RedisTokenDto redisTokenDto;
        if (value instanceof LinkedHashMap) {
            redisTokenDto = objectMapper.convertValue(value, RedisTokenDto.class);
        } else {
            redisTokenDto = (RedisTokenDto) value;
        }

        if (redisTokenDto == null) {
            throw new TokenNotFoundException("❌ Token for username not found: " + username);
        }

        return redisTokenDto.token();
    }


    public boolean isRedisAvailable() {
        try {
            String pingResponse = redisTemplate.getConnectionFactory().getConnection().ping();
            return "PONG".equals(pingResponse);
        } catch (Exception ex) {
            log.error("❌ Redis is unavailable: {}", ex.getMessage());
            return false;
        }
    }
}
