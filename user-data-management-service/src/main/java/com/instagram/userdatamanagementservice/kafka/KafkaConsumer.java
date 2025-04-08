package com.instagram.userdatamanagementservice.kafka;

import com.instagram.userdatamanagementservice.dto.kafka.UserAuthenticationDto;
import com.instagram.userdatamanagementservice.dto.kafka.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final BlockingQueue<UserRegistrationDto> registrationQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<UserAuthenticationDto> authenticationQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${topics.authentication-service.user-management-service.consumer.registration}", groupId = "authentication-service")
    public void consumeNewUser(UserRegistrationDto userRegistrationDto) {
        log.info("Received new user {}", userRegistrationDto);
        registrationQueue.offer(userRegistrationDto);
    }

    @KafkaListener(topics = "${topics.authentication-service.user-management-service.consumer.authentication}", groupId = "authentication-service")
    public void consumeAuthentication(UserAuthenticationDto userAuthenticationDto) {
        authenticationQueue.offer(userAuthenticationDto);
    }

    public UserRegistrationDto pollRegistrationDto() {
        return registrationQueue.poll();
    }

    public UserAuthenticationDto pollAuthenticationDto() {
        return authenticationQueue.poll();
    }
}
