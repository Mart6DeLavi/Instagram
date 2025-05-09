package com.instagram.callsservice.controller;

import com.instagram.callsservice.dto.CreateCallRequestDto;
import com.instagram.callsservice.dto.UpdateCallStatusRequestDto;
import com.instagram.callsservice.entity.Call;
import com.instagram.callsservice.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calls")
public class CallController {

    private final CallService callService;

    @PostMapping("/create")
    public Call createCall(@RequestBody CreateCallRequestDto request) {
        return callService.createCall(request.callerId(), request.calleeId(), request.type());
    }

    @PostMapping("/{callId}/status")
    public Call updateCallStatus(@PathVariable Long callId, @RequestBody UpdateCallStatusRequestDto request) {
        return callService.updateCallStatus(callId, request.status());
    }

    @GetMapping("/history/{userId}")
    public List<Call> getCallHistory(@PathVariable Long userId) {
        return callService.getCallHistory(userId);
    }
}
