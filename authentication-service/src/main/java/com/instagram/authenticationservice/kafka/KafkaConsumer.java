package com.instagram.authenticationservice.kafka;

import com.instagram.authenticationservice.dto.kafka.UserAuthenticationDto;
import com.instagram.authenticationservice.model.UserExists;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class KafkaConsumer {

    private final BlockingQueue<UserExists> authenticationQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${topics.user-data-management-service.consumer}", groupId = "user-data-management-service")
    private void authenticateUser(UserExists userExists) {
        authenticationQueue.offer(userExists);
    }

    public UserExists pollAuthentication() {
        return authenticationQueue.poll();
    }
}
