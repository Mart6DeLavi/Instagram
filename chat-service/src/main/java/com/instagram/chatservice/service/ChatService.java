package com.instagram.chatservice.service;

import com.instagram.chatservice.document.Chat;
import com.instagram.chatservice.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public Chat createChat(List<Long> participants) {
        return chatRepository.save(Chat.builder()
                .participants(participants)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<Chat> getChatsByUserId(Long userId) {
        return chatRepository.findAllByParticipant(userId);
    }

    public Optional<Chat> getChatById(String chatId) {
        return Optional.ofNullable(chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("No chat with chatId")));
    }
}

