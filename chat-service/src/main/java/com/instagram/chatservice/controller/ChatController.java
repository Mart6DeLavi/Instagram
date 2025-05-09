package com.instagram.chatservice.controller;

import com.instagram.chatservice.document.Chat;
import com.instagram.chatservice.dto.ChatCreationDto;
import com.instagram.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/create")
    public Chat createChat(@RequestBody ChatCreationDto chat) {
        return chatService.createChat(chat.participants());
    }

    @GetMapping("/all/{userId}")
    public List<Chat> getChatsByUserId(@PathVariable Long userId) {
        return chatService.getChatsByUserId(userId);
    }

    @GetMapping("/chat/{chatId}")
    public Optional<Chat> getChatById(@PathVariable String chatId) {
        return chatService.getChatById(chatId);
    }
}
