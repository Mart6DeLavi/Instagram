package com.instagram.searchservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.instagram.dto.kafka.IndexingPostInformationDto;
import com.instagram.searchservice.exception.FailedToIndexPostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchClient elasticsearchClient;

    @KafkaListener(topics = "search-indexing", groupId = "search-service", containerFactory = "kafkaListenerContainerFactory")
    public void indexPost(IndexingPostInformationDto dto) {
        try {
            log.info("Received indexing post : {}", dto);
            elasticsearchClient.index(i -> i
                    .index("posts")
                    .id(dto.postId())
                    .document(dto)
            );
        } catch (IOException ex ) {
            throw new FailedToIndexPostException("Failed to index post: " + dto.postId(), ex);
        }
    }
}
