package com.instagram.callsservice.service;


import com.instagram.callsservice.entity.Call;
import com.instagram.callsservice.model.CallStatus;
import com.instagram.callsservice.model.CallType;
import com.instagram.callsservice.repository.CallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;

    public Call createCall(Long callerId, Long calleeId, CallType type) {
        return callRepository.save(Call.builder()
                .callerId(callerId)
                .calleeId(calleeId)
                .type(type)
                .status(CallStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public Call updateCallStatus(Long callId, CallStatus status) {
        Call call = callRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));

        call.setStatus(status);
        if (status == CallStatus.ACCEPTED) {
            call.setAcceptedAt(LocalDateTime.now());
        } else if (status == CallStatus.ENDED || status == CallStatus.REJECTED) {
            call.setEndedAt(LocalDateTime.now());
        }

        return callRepository.save(call);
    }

    public List<Call> getCallHistory(Long userId) {
        return callRepository.findCallHistoryByUserId(userId);
    }
}
