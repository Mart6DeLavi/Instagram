package com.instagram.callsservice.dto;

import com.instagram.callsservice.model.CallType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignalMessageDto(
        Long callerId,
        Long calleeId,
        Long callId,
        String type,
        CallType callType,
        LocalDateTime timestamp
) {
}
