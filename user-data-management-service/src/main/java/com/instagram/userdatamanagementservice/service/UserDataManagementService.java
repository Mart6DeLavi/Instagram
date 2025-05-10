package com.instagram.userdatamanagementservice.service;

import com.instagram.dto.UserResponseDto;
import com.instagram.dto.kafka.UserAuthenticationDto;
import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.userdatamanagementservice.config.SecurityConfig;
import com.instagram.userdatamanagementservice.entity.User;
import com.instagram.userdatamanagementservice.exception.UserAlreadyExistsException;
import com.instagram.exception.UserNotFoundException;
import com.instagram.userdatamanagementservice.exception.UserRegistrationFailedException;
import com.instagram.userdatamanagementservice.kafka.KafkaConsumer;
import com.instagram.userdatamanagementservice.mapper.EntityMapper;
import com.instagram.userdatamanagementservice.model.UpdatesStatus;
import com.instagram.userdatamanagementservice.model.UserExists;
import com.instagram.userdatamanagementservice.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    public UserResponseDto findUserByUsername(@NonNull String username) {
        return userRepository.findUserByUsername(username)
                .map(mapper::mapToResponse)
                .orElseThrow(() -> new UserNotFoundException("No such user with username: " + username));
    }

    public String getUsernameByUserId(@NonNull Long userId) {
        return Optional.of(userRepository.getUsernameByUserId(userId))
                .orElseThrow(() -> new UserNotFoundException("No such user with id: " + userId));
    }

    public Long getUserIdByUsername(@NonNull String username) {
        return Optional.of(userRepository.getUserIdByUsername(username))
                .orElseThrow(() -> new UserNotFoundException("No such user with username: " + username));
    }

    public UserExists authenticateUser(@NonNull UserAuthenticationDto userAuthenticationDto) {
        return userRepository.findUserByUsername(userAuthenticationDto.username())
                .filter(user -> securityConfig.passwordEncoder().matches(userAuthenticationDto.password(), user.getPassword()))
                .map(user -> UserExists.FOUND)
                .orElse(UserExists.NOTFOUND);
    }

    @Async
    public CompletableFuture<Void> createNewUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.findUserByUsername(userRegistrationDto.username()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists: " + userRegistrationDto.username());
        }

        try {
            var newUser = kafkaConsumer.pollRegistrationDto();
            userRepository.save(mapper.mapToEntity(newUser));
            log.info("✅ User created asynchronously: {}", userRegistrationDto.username());
        } catch (DataIntegrityViolationException e) {
            throw new UserRegistrationFailedException("User registration failed: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<UpdatesStatus> updateUserInformationAsync(@NonNull UserRegistrationDto dto) {
        UpdatesStatus status = updateUserInformation(dto);
        return CompletableFuture.completedFuture(status);
    }

    public UpdatesStatus updateUserInformation(@NonNull UserRegistrationDto dto) {
        return userRepository.findUserByUsername(dto.username())
                .map(user -> updateUsersFields(user, dto))
                .map(userRepository::saveAndFlush)
                .map(user -> UpdatesStatus.UPDATED)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + dto.username()));
    }

    @Async
    public CompletableFuture<String> deleteUserAsync(String username) {
        userRepository.findUserByUsername(username)
                .ifPresent(userRepository::delete);
        return CompletableFuture.completedFuture("User deleted successfully");
    }

    private User updateUsersFields(@NonNull User user, @NonNull UserRegistrationDto dto) {
        Map<Supplier<Object>, Consumer<Object>> fieldMapping = Map.of(
                dto::firstName, value -> user.setFirstName((String) value),
                dto::lastName, value -> user.setLastName((String) value),
                dto::email, value -> user.setEmail((String) value),
                dto::username, value -> user.setUsername((String) value)
        );
        fieldMapping.forEach((getter, setter) -> Optional.ofNullable(getter.get()).ifPresent(setter));
        return user;
    }
}
