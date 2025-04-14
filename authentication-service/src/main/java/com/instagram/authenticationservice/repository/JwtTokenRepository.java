package com.instagram.authenticationservice.repository;

import com.instagram.authenticationservice.entity.JwtTokenRedisBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenRedisBackup, Long> {

    @Query("""
            SELECT backup 
            FROM JwtTokenRedisBackup backup 
            WHERE 
            backup.username = :username
    """)
    Optional<JwtTokenRedisBackup> findTokenByUsername(@Param("username") String username);
}
