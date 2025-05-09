package com.instagram.chatservice.repository;

import com.instagram.chatservice.document.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    @Query(value = "{ 'chatId': ?0 }", sort = "{ 'sentAt': 1 }")
    List<Message> findMessagesByChatId(String chatId);

}
