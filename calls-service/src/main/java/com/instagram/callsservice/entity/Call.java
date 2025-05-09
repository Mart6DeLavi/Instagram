package com.instagram.callsservice.entity;

import com.instagram.callsservice.model.CallStatus;
import com.instagram.callsservice.model.CallType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "calls")
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long callerId;

    @Column(nullable = false)
    private Long calleeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus status;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime acceptedAt;

    private LocalDateTime endedAt;
}
