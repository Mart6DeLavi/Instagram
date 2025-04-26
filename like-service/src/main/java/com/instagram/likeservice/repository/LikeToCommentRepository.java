package com.instagram.likeservice.repository;

import com.instagram.likeservice.entity.LikeToComment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeToCommentRepository extends JpaRepository<LikeToComment, Long> {

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM LikeToComment l
        WHERE
        l.commentId = :commentId
        AND
        l.userId = :userId
    """)
    void deleteLikeToCommentByCommentIdAndUserId(@Param("commentId") String commentId, @Param("userId") Long userId);

    @Query("""
        SELECT COUNT(l) > 0
        FROM LikeToComment l
        WHERE
        l.commentId = :commentId
        AND
         l.userId = :userId
    """)
    boolean existsByCommentIdAndUserId(@Param("commentId") String commentId, @Param("userId") Long userId);
}
