package com.instagram.userdatamanagementservice.kafka;

import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final BlockingQueue<UserRegistrationDto> registrationQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${topics.authentication-service.user-management-service.consumer.registration}", groupId = "authentication-service")
    public void consumeNewUser(UserRegistrationDto userRegistrationDto) {
        log.info("Received new user {}", userRegistrationDto);
        registrationQueue.offer(userRegistrationDto);
        applicationEventPublisher.publishEvent(new UserRegistrationEvent(userRegistrationDto));
    }

    public UserRegistrationDto pollRegistrationDto() {
        return registrationQueue.poll();
    }

}
