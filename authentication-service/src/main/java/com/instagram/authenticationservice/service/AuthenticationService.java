package com.instagram.authenticationservice.service;

import com.instagram.authenticationservice.config.client.UserDataManagementClient;
import com.instagram.authenticationservice.kafka.KafkaProducer;
import com.instagram.authenticationservice.mapper.EntityMapper;
import com.instagram.authenticationservice.model.UserExists;
import com.instagram.authenticationservice.repository.JwtTokenRepository;
import com.instagram.authenticationservice.util.JwtTokenUtils;
import com.instagram.authenticationservice.util.RedisUtils;
import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.dto.redis.RedisTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RedisUtils redisUtil;
    private final KafkaProducer kafkaProducer;
    private final JwtTokenUtils jwtTokenUtils;
    private final JwtTokenRepository jwtTokenRepository;
    private final UserDataManagementClient userDataManagementClient;

    public void registerNewUser(UserRegistrationDto userRegistrationDto) {
        try {
            kafkaProducer.sendRegistrationRequest(userRegistrationDto);
            log.info("✅ Message for register new user sent to user-data-management-service successfully");
        } catch (Exception ex) {
            log.error("❌ Message for register new user sent to user-data-management-service failed");
            throw new RuntimeException(ex);
        }
    }

    public ResponseEntity<String> authenticateUser(UserAuthenticationDto userAuthenticationDto) {
        String token;

        try {
            UserExists userExists = userDataManagementClient.authenticateUser(userAuthenticationDto);
            if (userExists.equals(UserExists.FOUND)) {
                if (redisUtil.isRedisAvailable()) {
                    token = jwtTokenUtils.generateToken(userAuthenticationDto);
                    redisUtil.saveTokenToRedis(userAuthenticationDto.username(), token);
                    return ResponseEntity.ok(token);
                } else if (!redisUtil.isRedisAvailable()){
                    token = jwtTokenUtils.generateToken(userAuthenticationDto);
                    var redisBackupDto = RedisTokenDto.builder().username(userAuthenticationDto.username()).token(token).build();
                    jwtTokenRepository.save(EntityMapper.mapToRedisBackup(redisBackupDto));
                    return ResponseEntity.ok(token);
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("User with username: %s not found", userAuthenticationDto.username()));

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
