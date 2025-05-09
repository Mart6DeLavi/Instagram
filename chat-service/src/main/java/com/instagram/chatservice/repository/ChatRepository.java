package com.instagram.chatservice.repository;

import com.instagram.chatservice.document.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    @Query(" {'participants' :  ?0 }")
    List<Chat> findAllByParticipant(Long userId);
}
