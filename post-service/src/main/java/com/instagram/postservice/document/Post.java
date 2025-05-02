package com.instagram.postservice.document;

import com.instagram.dto.kafka.Location;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Document(collection = "posts")
public class Post {

    @Id
    private String id;
    private Long userId;
    private List<Media> mediaList;
    private String description;
    private Location location;
    private List<String> tags;
    private Integer likesCount;
    private Integer commentsCount;
    private Date createdAt;
}
