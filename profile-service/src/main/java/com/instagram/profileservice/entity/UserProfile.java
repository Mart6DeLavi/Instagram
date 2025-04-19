package com.instagram.profileservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_profile")
public class UserProfile{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String username;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Builder.Default
    @Column(name = "number_of_posts")
    private Integer numberOfPosts = 0;

    @Builder.Default
    @Column(name = "number_of_subscribers")
    private Integer numberOfSubscribers = 0;

    @Builder.Default
    @Column(name = "number_of_subscriptions")
    private Integer numberOfSubscriptions = 0;

    @Column(name = "about_myself")
    private String aboutMyself;

    @Builder.Default
    private Boolean isPublic = true;

    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
}
