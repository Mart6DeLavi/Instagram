package com.instagram.chatservice.dto;


public record MessageCreationDto(
    String chatId,
    Long senderId,
    String text
) {
}
