package com.instagram.callsservice.controller;

import com.instagram.callsservice.dto.SignalMessageDto;
import com.instagram.callsservice.entity.Call;
import com.instagram.callsservice.model.CallStatus;
import com.instagram.callsservice.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class CallWebSocketController {

    private final CallService callService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/call.initiate")
    public void initiateCall(@Payload SignalMessageDto message) {
        Call call = callService.createCall(message.callerId(), message.calleeId(), message.callType());

        SignalMessageDto response = SignalMessageDto.builder()
                .callerId(call.getCallerId())
                .calleeId(call.getCalleeId())
                .callId(call.getId())
                .callType(call.getType())
                .type("INITIATE")
                .timestamp(call.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/call/user." + call.getCalleeId(), response);
    }

    @MessageMapping("/call.accept")
    public void acceptCall(@Payload SignalMessageDto message) {
        Call call = callService.updateCallStatus(message.callId(), CallStatus.ACCEPTED);

        SignalMessageDto response = SignalMessageDto.builder()
                .callerId(call.getCallerId())
                .calleeId(call.getCalleeId())
                .callId(call.getId())
                .callType(call.getType())
                .type("ACCEPT")
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/call/user." + call.getCallerId(), response);
    }

    @MessageMapping("/call.end")
    public void endCall(@Payload SignalMessageDto message) {
        Call call = callService.updateCallStatus(message.callId(), CallStatus.ENDED);

        SignalMessageDto response = SignalMessageDto.builder()
                .callerId(call.getCallerId())
                .calleeId(call.getCalleeId())
                .callId(call.getId())
                .callType(call.getType())
                .type("END")
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/call/user." + call.getCallerId(), response);
        messagingTemplate.convertAndSend("/topic/call/user." + call.getCalleeId(), response);
    }
}