package com.instagram.authenticationservice.kafka;

import com.instagram.authenticationservice.dto.kafka.UserAuthenticationDto;
import com.instagram.authenticationservice.dto.kafka.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topics.user-data-management-service.producer.registration}")
    private String registrationTopic;

    @Value("${topics.user-data-management-service.producer.authentication}")
    private String authenticationTopic;

    public void sendRegistrationRequest(UserRegistrationDto data) {
        try {
            kafkaTemplate.send(registrationTopic, data);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sendAuthenticationRequest(UserAuthenticationDto data) {
        try {
            kafkaTemplate.send(authenticationTopic, data);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
