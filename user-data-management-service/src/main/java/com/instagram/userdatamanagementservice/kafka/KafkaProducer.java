package com.instagram.userdatamanagementservice.kafka;

import com.instagram.userdatamanagementservice.config.SecurityConfig;
import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.userdatamanagementservice.entity.User;
import com.instagram.userdatamanagementservice.model.UserExists;
import com.instagram.userdatamanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, UserAuthenticationDto> kafkaTemplate;
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;

    public UserExists authenticateUser(UserAuthenticationDto userAuthenticationDto) {
        Optional<User> existedUser = userRepository.findUserByUsername(userAuthenticationDto.username());

        if (existedUser.isPresent()) {
            try {
                if (existedUser.get().getPassword().equals(securityConfig.passwordEncoder().encode(userAuthenticationDto.password()))) {
                    return UserExists.FOUND;
                }
            } catch (Exception e) {

            }
        }
        return UserExists.NOTFOUND;
    }
}
