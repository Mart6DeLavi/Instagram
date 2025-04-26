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
@Table(name = "like_to_post", uniqueConstraints = {
        @UniqueConstraint(name = "unique_like_to_post_fields", columnNames = {"user_id", "post_id"})
})
public class LikeToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "created_at")
    private Timestamp createdAt;
}
