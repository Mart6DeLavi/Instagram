package com.instagram.chatservice.service;

import com.instagram.chatservice.document.Message;
import com.instagram.chatservice.dto.MessageCreationDto;
import com.instagram.chatservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message createMessage(MessageCreationDto message) {
        return messageRepository.save(Message.builder()
                .chatId(message.chatId())
                .text(message.text())
                .senderId(message.senderId())
                .createAt(LocalDateTime.now())
                .build());
    }

    public List<Message> getMessagesByChatId(String chatId) {
        return messageRepository.findMessagesByChatId(chatId);
    }
}
