package com.instagram.userdatamanagementservice.service;

import com.instagram.userdatamanagementservice.config.SecurityConfig;
import com.instagram.dto.UserResponseDto;
import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.userdatamanagementservice.entity.User;
import com.instagram.userdatamanagementservice.exception.UserAlreadyExistsException;
import com.instagram.userdatamanagementservice.exception.UserNotFoundException;
import com.instagram.userdatamanagementservice.exception.UserRegistrationFailedException;
import com.instagram.userdatamanagementservice.kafka.KafkaConsumer;
import com.instagram.userdatamanagementservice.mapper.EntityMapper;
import com.instagram.userdatamanagementservice.model.UpdatesStatus;
import com.instagram.userdatamanagementservice.model.UserExists;
import com.instagram.userdatamanagementservice.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataManagementService {
    private final UserRepository userRepository;
    private final KafkaConsumer kafkaConsumer;
    private final EntityMapper mapper;
    private final SecurityConfig securityConfig;
    private final KafkaTemplate<String, UserExists> kafkaTemplate;

    @Value("${topics.authentication-service.user-management-service.producer.authentication}")
    private static String authenticationAnswerTopic;

    public UserResponseDto findUserByUsername(@NonNull String username) {
        Optional<User> existedUser = Optional.of(userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("No such user with username: %s", username)
                )));
        return mapper.mapToResponse(existedUser.orElse(null));
    }

    public void createNewUser(UserRegistrationDto userRegistrationDto) {
        Optional<User> existedUser = userRepository.findUserByUsername(userRegistrationDto.username());
        if (existedUser.isPresent()) {
            new UserAlreadyExistsException(
                    String.format("User %s already exist", userRegistrationDto.username()));
        }

        try {
                var newUser = kafkaConsumer.pollRegistrationDto();
                userRepository.save(mapper.mapToEntity(newUser));
                log.info("✅ User registered successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new UserRegistrationFailedException(
                    String.format("User %s already exists", userRegistrationDto.username())
            );
        }
    }

    public UserExists authenticateUser() {
        UserAuthenticationDto userAuthenticationDto = kafkaConsumer.pollAuthenticationDto();
        Optional<User> existedUser = userRepository.findUserByUsername(userAuthenticationDto.username());

        if (existedUser.isPresent()) {
            try {
                if (existedUser.get().getPassword().equals(securityConfig.passwordEncoder().encode(userAuthenticationDto.password()))) {
                    kafkaTemplate.send(authenticationAnswerTopic, UserExists.FOUND);
                    return UserExists.FOUND;
                }
            } catch (Exception e) {
                throw new UserAlreadyExistsException(
                        String.format("Cannot authenticate user: %s", userAuthenticationDto.username())
                );
            }
        }
        return UserExists.NOTFOUND;
    }

    public UpdatesStatus updateUserInformation(@NonNull UserRegistrationDto userRegistrationDto) {
        try {
            return userRepository.findUserByUsername(userRegistrationDto.username())
                    .map(user -> updateUsersFields(user, userRegistrationDto))
                    .map(userRepository::saveAndFlush)
                    .map(updatedStatus -> UpdatesStatus.UPDATED)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("User wuth username %s not found", userRegistrationDto.username())
                    ));
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Failed to update user information due to data integrity violation", ex);
        }
    }

    public String deleteUser(String username) {
        var existedUser = userRepository.findUserByUsername(username);
        existedUser.ifPresent(userRepository::delete);
        return "User deleted successfully";
    }

    private User updateUsersFields(@NonNull User user, @NonNull UserRegistrationDto userRegistrationDto) {
        Map<Supplier<Object>, Consumer<Object>> fieldMapping = Map.of(
                userRegistrationDto::firstName, value -> user.setFirstName((String) value),
                userRegistrationDto::lastName, value -> user.setLastName((String) value),
                userRegistrationDto::email, value -> user.setEmail((String) value),
                userRegistrationDto::username, value -> user.setUsername((String) value)
        );

        fieldMapping.forEach((getter, setter) -> Optional.ofNullable(getter.get()).ifPresent(setter));
        return user;
    }
}
