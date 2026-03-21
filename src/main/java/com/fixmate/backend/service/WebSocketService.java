package com.fixmate.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;


    public void sendRefreshSignal(String topic) {
        // We send a simple "REFRESH" string.
        // The frontend will see this and know to re-fetch data.
        messagingTemplate.convertAndSend("/topic/" + topic, "REFRESH");
    }
}