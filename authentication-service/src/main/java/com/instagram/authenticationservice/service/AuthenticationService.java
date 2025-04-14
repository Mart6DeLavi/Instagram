package com.instagram.authenticationservice.service;

import com.instagram.authenticationservice.config.client.UserDataManagementClient;
import com.instagram.authenticationservice.entity.JwtTokenRedisBackup;
import com.instagram.authenticationservice.exception.TokenNotFoundException;
import com.instagram.authenticationservice.kafka.KafkaProducer;
import com.instagram.authenticationservice.mapper.EntityMapper;
import com.instagram.authenticationservice.model.UserExists;
import com.instagram.authenticationservice.repository.JwtTokenRepository;
import com.instagram.authenticationservice.util.JwtTokenUtils;
import com.instagram.authenticationservice.util.RedisUtils;
import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.dto.redis.RedisTokenDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.util.annotation.NonNull;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RedisUtils redisUtil;
    private final KafkaProducer kafkaProducer;
    private final JwtTokenUtils jwtTokenUtils;
    private final JwtTokenRepository jwtTokenRepository;
    private final UserDataManagementClient userDataManagementClient;

    @Transactional
    public void registerNewUser(@NonNull UserRegistrationDto userRegistrationDto) {
        try {
            kafkaProducer.sendRegistrationRequest(userRegistrationDto);
            log.info("✅ Message for register new user sent to user-data-management-service successfully");
        } catch (Exception ex) {
            log.error("❌ Message for register new user sent to user-data-management-service failed");
            throw new RuntimeException(ex);
        }
    }

    @Transactional
    public ResponseEntity<String> authenticateUser(@NonNull UserAuthenticationDto userAuthenticationDto) {
        String token;

        try {
            UserExists userExists = userDataManagementClient.authenticateUser(userAuthenticationDto);
            if (userExists.equals(UserExists.FOUND)) {
                log.info("✅ User found. Continue process...");
                if (redisUtil.isRedisAvailable()) {
                    token = jwtTokenUtils.generateToken(userAuthenticationDto);
                    redisUtil.saveTokenToRedis(userAuthenticationDto.username(), token);
                    log.info("✅ Token generated successfully and returned to user");
                    return ResponseEntity.ok(token);
                } else if (!redisUtil.isRedisAvailable()){
                    log.warn("⚠️ Redis is unavailable. Saving tokens to jwt_backup table");
                    token = jwtTokenUtils.generateToken(userAuthenticationDto);
                    var redisBackupDto = RedisTokenDto.builder()
                            .username(userAuthenticationDto.username())
                            .token(token)
                            .build();
                    jwtTokenRepository.save(EntityMapper.mapToRedisBackup(redisBackupDto));
                    log.info("✅ Token generated and returned to user");
                    return ResponseEntity.ok(token);
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("❌ User with username: %s not found", userAuthenticationDto.username()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String findTokenByUsername(@NonNull String username) {
        if (redisUtil.isRedisAvailable()) {
            var userToken = redisUtil.findTokenByUsername(username);
            if (userToken != null) {
                return userToken;
            } else {
                throw new TokenNotFoundException(String.format("❌ Token not found for username: %s", username));
            }
        } else if (!redisUtil.isRedisAvailable()){
            Optional<JwtTokenRedisBackup> userToken = Optional.ofNullable(jwtTokenRepository.findTokenByUsername(username)
                    .orElseThrow(() -> new TokenNotFoundException("❌ No token for username: " + username)));
            if (userToken.isPresent()) {
                return userToken.get().getToken();
            }
        }
        return HttpStatus.NO_CONTENT.getReasonPhrase();
    }
}
