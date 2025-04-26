package com.instagram.likeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "comment-service", url = "http://localhost:8005")
public interface CommentsServiceClient {

    @GetMapping("/api/v1/comments/{commentId}")
    boolean isCommentExist(@PathVariable String commentId);
}
