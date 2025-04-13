package com.instagram.userdatamanagementservice.event;

import com.instagram.dto.kafka.UserRegistrationDto;
import com.instagram.userdatamanagementservice.kafka.UserRegistrationEvent;
import com.instagram.userdatamanagementservice.service.UserDataManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationEventListener {
    private final UserDataManagementService userDataManagementService;

    @EventListener
    public void onUserRegistration(UserRegistrationEvent event) {
        UserRegistrationDto dto = event.dto();
        userDataManagementService.createNewUser(dto);
    }
}
