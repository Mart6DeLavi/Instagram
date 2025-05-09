package com.instagram.chatservice.controller;

import com.instagram.chatservice.dto.MessageCreationDto;
import com.instagram.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final MessageService messageService;

    @MessageMapping("/chat.send")
    @SendTo("/topic/message/{chatId}")
    public MessageCreationDto sendMessage(@Payload MessageCreationDto message) {
        messageService.createMessage(message);
        return message;
    }
}
