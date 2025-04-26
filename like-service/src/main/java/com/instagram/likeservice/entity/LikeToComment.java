package com.instagram.likeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "like_to_comment", uniqueConstraints = {
        @UniqueConstraint(name = "unique_like_to_comment_fields", columnNames = {"user_id", "comment_id"})
})
public class LikeToComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "comment_id")
    private String commentId;

    @Column(name = "created_at")
    private Timestamp createdAt;
}
