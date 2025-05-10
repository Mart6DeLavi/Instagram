package com.instagram.chatservice.service;

import com.instagram.chatservice.document.Chat;
import com.instagram.chatservice.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    public ChatServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateChatSuccessfully() {
        // Arrange
        List<Long> participants = Arrays.asList(1L, 2L);
        Chat chat = Chat.builder()
                .id("123")
                .participants(participants)
                .createdAt(LocalDateTime.now())
                .build();

        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        // Act
        Chat result = chatService.createChat(participants);

        // Assert
        assertNotNull(result);
        assertEquals(participants, result.getParticipants());
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void shouldGetChatsByUserIdSuccessfully() {
        // Arrange
        Long userId = 1L;
        List<Chat> chats = Arrays.asList(
                Chat.builder().id("chat1").participants(Arrays.asList(1L, 2L)).build(),
                Chat.builder().id("chat2").participants(Arrays.asList(1L, 3L)).build()
        );

        when(chatRepository.findAllByParticipant(userId)).thenReturn(chats);

        // Act
        List<Chat> result = chatService.getChatsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(chat -> chat.getParticipants().contains(userId)));
        verify(chatRepository, times(1)).findAllByParticipant(userId);
    }

    @Test
    void shouldGetChatByIdSuccessfully() {
        // Arrange
        String chatId = "123";
        Chat chat = Chat.builder()
                .id(chatId)
                .participants(Arrays.asList(1L, 2L))
                .createdAt(LocalDateTime.now())
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        // Act
        Optional<Chat> result = chatService.getChatById(chatId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(chatId, result.get().getId());
        verify(chatRepository, times(1)).findById(chatId);
    }

    @Test
    void shouldThrowExceptionWhenChatNotFoundById() {
        // Arrange
        String chatId = "not_existing";

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            chatService.getChatById(chatId);
        });
        assertEquals("No chat with chatId", exception.getMessage());
        verify(chatRepository, times(1)).findById(chatId);
    }
}