package com.instagram.postservice.repository;

import com.instagram.postservice.document.Post;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    @Query(value = "{ 'userId' :  ?0 }", sort = "{ 'createdAt' :  -1 }")
    List<Post> findAllByUserId(Long userId);

    @Query(value = "{ '_id' : ?0 }")
    Optional<Post> getPostByPostId(ObjectId postId);
}
