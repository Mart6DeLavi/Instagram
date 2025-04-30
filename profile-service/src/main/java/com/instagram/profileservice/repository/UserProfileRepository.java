package com.instagram.profileservice.repository;

import com.instagram.profileservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("""
        SELECT u
        FROM UserProfile u
        WHERE
        u.userId = :userId
    """)
    Optional<UserProfile> getUserProfileByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT u
        FROM UserProfile u
        WHERE
        u.userId IN :userIds
    """)
    List<UserProfile> findAllByUserIds(List<Long> userIds);
}

