package com.instagram.followservice.repository;

import com.instagram.followservice.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Transactional
    @Modifying
    @Query("""
        DELETE
        FROM Subscription subscription
        WHERE
        subscription.followerId = :followerId
        AND
        subscription.followingId = :followingId
    """)
    void deleteByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    @Query("""
        SELECT subscription.followerId
        FROM Subscription subscription
        WHERE
        subscription.followingId = :userId
    """)
    List<Long> getAllFollowerIdsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT subscription.followingId
        FROM Subscription subscription
        WHERE
        subscription.followerId = :userId
    """)
    List<Long> getAllFollowingIdsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT COUNT(subscription)
        FROM Subscription subscription
        WHERE
        subscription.followerId = :followerId
        AND
        subscription.followingId = :followingId
    """)
    long countByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
}
