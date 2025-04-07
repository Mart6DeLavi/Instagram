package com.instagram.authenticationservice.service;

import com.instagram.authenticationservice.dto.kafka.UserAuthenticationDto;
import com.instagram.authenticationservice.dto.kafka.UserRegistrationDto;
import com.instagram.authenticationservice.kafka.KafkaConsumer;
import com.instagram.authenticationservice.kafka.KafkaProducer;
import com.instagram.authenticationservice.model.UserExists;
import com.instagram.authenticationservice.util.JwtTokenUtils;
import com.instagram.authenticationservice.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RedisUtils redisUtil;
    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;
    private final JwtTokenUtils jwtTokenUtils;

    public void registerNewUser(UserRegistrationDto userRegistrationDto) {
        try {
            kafkaProducer.sendRegistrationRequest(userRegistrationDto);
            log.info("✅ Message for register new user sent to user-data-management-service successfully");
        } catch (Exception ex) {
            log.error("❌ Message for register new user sent to user-data-management-service failed");
            throw new RuntimeException(ex);
        }
    }

    public String authenticateUser(UserAuthenticationDto userAuthenticationDto) {
        String token = null;
        try {
            try {
                kafkaProducer.sendAuthenticationRequest(userAuthenticationDto);
                log.info("✅ Message for authenticate user sent to user-data-management-service successfully");
            } catch (Exception ex) {
                log.error("❌ Message for authenticate user sent to user-data-management-service failed");
                throw new RuntimeException(ex);
            }
            UserExists userExists = kafkaConsumer.pollAuthentication();
            if (userExists.equals(UserExists.FOUND)) {
                if (redisUtil.isRedisAvailable()) {
                    token = jwtTokenUtils.generateToken(userAuthenticationDto);
                    redisUtil.saveTokenToRedis(userAuthenticationDto.username(), token);
                }
            }
            return token;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
