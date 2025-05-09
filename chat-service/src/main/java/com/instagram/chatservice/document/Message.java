package com.instagram.chatservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String chatId;
    private Long senderId;
    private String text;
    private LocalDateTime createAt;
}
