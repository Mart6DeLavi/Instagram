package com.instagram.authenticationservice.repository;

import com.instagram.authenticationservice.entity.JwtTokenRedisBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenRedisBackup, Long> {
}
