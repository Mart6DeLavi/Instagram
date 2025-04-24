package com.instagram.commentsservice.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(value = "comment")
public class Comment {
    @Id
    private String id;

    private Long userId;
    private String postId;
    private String username;
    private String commentText;

    @Builder.Default
    private int likesCount = 0;

    @Builder.Default
    private boolean isEdited = false;
    private Date createdAt;
    private Date updatedAt;
}
