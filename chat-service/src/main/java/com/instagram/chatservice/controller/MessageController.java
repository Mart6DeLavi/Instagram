package com.instagram.chatservice.controller;


import com.instagram.chatservice.document.Message;
import com.instagram.chatservice.dto.MessageCreationDto;
import com.instagram.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody MessageCreationDto request) {
        return messageService.createMessage(request);
    }

    @GetMapping("/{chatId}")
    public List<Message> getMessagesByChatId(@PathVariable String chatId) {
        return messageService.getMessagesByChatId(chatId);
    }
}

