package com.instagram.callsservice.dto;

import com.instagram.callsservice.model.CallStatus;

public record UpdateCallStatusRequestDto(
        CallStatus status
) {
}
