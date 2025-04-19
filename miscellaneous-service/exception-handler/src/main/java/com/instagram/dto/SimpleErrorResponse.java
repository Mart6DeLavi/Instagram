package com.instagram.dto;

import java.time.LocalDateTime;

public record SimpleErrorResponse(
        LocalDateTime timestamp,
        String message
) {
}
