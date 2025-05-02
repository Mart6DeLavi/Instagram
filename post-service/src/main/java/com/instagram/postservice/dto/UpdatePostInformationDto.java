package com.instagram.postservice.dto;

import java.util.List;

public record UpdatePostInformationDto(
        String description,
        List<String> tags
) {
}
