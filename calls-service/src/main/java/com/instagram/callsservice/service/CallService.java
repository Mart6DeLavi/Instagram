package com.instagram.callsservice.service;


import com.instagram.callsservice.entity.Call;
import com.instagram.callsservice.model.CallStatus;
import com.instagram.callsservice.model.CallType;
import com.instagram.callsservice.repository.CallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;

    @Async
    public CompletableFuture<Call> createCall(Long callerId, Long calleeId, CallType type) {
        Call call = Call.builder()
                .callerId(callerId)
                .calleeId(calleeId)
                .type(type)
                .status(CallStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        return CompletableFuture.completedFuture(callRepository.save(call));
    }

    @Async
    @Transactional
    public CompletableFuture<Call> updateCallStatus(Long callId, CallStatus status) {
        Call call = callRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));

        call.setStatus(status);
        if (status == CallStatus.ACCEPTED) {
            call.setAcceptedAt(LocalDateTime.now());
        } else if (status == CallStatus.ENDED || status == CallStatus.REJECTED) {
            call.setEndedAt(LocalDateTime.now());
        }

        return CompletableFuture.completedFuture(callRepository.save(call));
    }

    public List<Call> getCallHistory(Long userId) {
        return callRepository.findCallHistoryByUserId(userId);
    }
}
