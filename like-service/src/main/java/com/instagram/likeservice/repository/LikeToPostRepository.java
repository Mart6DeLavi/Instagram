package com.instagram.likeservice.repository;

import com.instagram.likeservice.entity.LikeToPost;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeToPostRepository extends JpaRepository<LikeToPost, Long> {

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM LikeToPost l
        WHERE
        l.postId = :postId
        AND
        l.userId = :userId
    """)
    void deleteLikeToPostByPostIdAndUserId(@Param("postId") String postId, @Param("userId") Long userId);

    @Query("""
        SELECT COUNT(l) > 0
        FROM LikeToPost l
        WHERE
        l.postId = :postId
        AND
        l.userId = :userId
    """)
    boolean existsByPostIdAndUserId(@Param("postId") String postId, @Param("userId") Long userId);

}
