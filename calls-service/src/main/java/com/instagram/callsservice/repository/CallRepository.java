package com.instagram.callsservice.repository;

import com.instagram.callsservice.entity.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {

    @Query("""
        SELECT c
        FROM Call c
        WHERE
        c.callerId = :userId
        OR
        c.calleeId = :userId
        ORDER BY
        c.createdAt DESC
    """)
    List<Call> findCallHistoryByUserId(@Param("userId") Long userId);
}
