package com.instagram.chatservice.dto;

import java.util.List;

public record ChatCreationDto(
        List<Long> participants
) {
}
