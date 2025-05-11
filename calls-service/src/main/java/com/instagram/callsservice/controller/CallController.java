package com.instagram.callsservice.controller;

import com.instagram.callsservice.dto.CreateCallRequestDto;
import com.instagram.callsservice.dto.UpdateCallStatusRequestDto;
import com.instagram.callsservice.entity.Call;
import com.instagram.callsservice.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calls")
public class CallController {

    private final CallService callService;

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<Call>> createCall(@RequestBody CreateCallRequestDto request) {
        return callService.createCall(request.callerId(), request.calleeId(), request.type())
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/{callId}/status")
    public CompletableFuture<ResponseEntity<Call>> updateStatus(@PathVariable Long callId, @RequestBody UpdateCallStatusRequestDto request) {
        return callService.updateCallStatus(callId, request.status())
                .thenApply(ResponseEntity::ok);
    }


    @GetMapping("/history/{userId}")
    public List<Call> getCallHistory(@PathVariable Long userId) {
        return callService.getCallHistory(userId);
    }
}
