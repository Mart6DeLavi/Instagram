package com.instagram.commentsservice.repository;

import com.instagram.commentsservice.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    @Query(value = "{ 'postId' :  ?0 }", sort = "{ 'createdAt' : -1 }")
    List<Comment> getAllCommentsByPostId(String postId);
}
