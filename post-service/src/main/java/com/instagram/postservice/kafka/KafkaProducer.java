package com.instagram.postservice.kafka;

import com.instagram.dto.kafka.IndexingPostInformationDto;
import com.instagram.dto.kafka.PostCreatedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, PostCreatedEventDto> kafkaTemplate;

    private static final String TOPIC = "post-events";

    public void sendPostCreatedEvent(Long userId) {
        PostCreatedEventDto event = new PostCreatedEventDto(userId);
        kafkaTemplate.send(TOPIC, event);
    }

    @Component
    @RequiredArgsConstructor
    public static class SearchIndexKafkaProducer {
        private final KafkaTemplate<String, IndexingPostInformationDto> kafkaTemplate;

        private static final String TOPIC = "search-indexing";

        public void sendIndexingEvent(IndexingPostInformationDto dto) {
            kafkaTemplate.send(TOPIC, dto.postId().toString(), dto);
        }
    }
}
