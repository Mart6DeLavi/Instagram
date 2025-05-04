package com.instagram.searchservice.controller;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.instagram.dto.kafka.IndexingPostInformationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
public class SearchController {

    private final ElasticsearchClient elasticsearchClient;

    @GetMapping
    public List<IndexingPostInformationDto> search(@RequestParam String query) throws IOException {
        SearchResponse<IndexingPostInformationDto> response = elasticsearchClient.search(s -> s
                        .index("posts")
                        .query(q -> q
                                .multiMatch(m -> m
                                        .query(query)
                                        .fields("description", "tags")
                                )
                        ),
                IndexingPostInformationDto.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .toList();
    }
}
