package com.instagram.authenticationservice.mapper;

import com.instagram.authenticationservice.entity.JwtTokenRedisBackup;
import com.instagram.dto.redis.RedisTokenDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public static JwtTokenRedisBackup mapToRedisBackup(RedisTokenDto redisTokenDto) {
        return new JwtTokenRedisBackup().builder()
                .username(redisTokenDto.username())
                .token(redisTokenDto.token())
                .build();
    }
}
