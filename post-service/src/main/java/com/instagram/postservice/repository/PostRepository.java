package com.instagram.postservice.repository;

import com.instagram.postservice.document.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    @Query(value = "{ 'userId' :  ?0 }", sort = "{ 'createdAt' :  -1 }")
    List<Post> findAllByUserId(Long userId);
}
