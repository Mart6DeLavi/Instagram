package com.instagram.userdatamanagementservice.repository;

import com.instagram.userdatamanagementservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u
        FROM User u
        WHERE
        u.username = :username
    """)
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("""
        SELECT u.id 
        FROM User u 
        WHERE 
        u.username = :username
    """)
    Long getUserIdByUsername(String username);

    @Query("""
        SELECT u.username 
        FROM User u 
        WHERE 
        u.id = :userId
    """)
    String getUsernameByUserId(@Param("userId") Long userId);
}
