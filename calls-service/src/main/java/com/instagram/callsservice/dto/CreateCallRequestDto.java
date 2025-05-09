package com.instagram.callsservice.dto;

import com.instagram.callsservice.model.CallType;

public record CreateCallRequestDto(
        Long callerId,
        Long calleeId,
        CallType type
) {
}
