package com.sparta.picboy.WebSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class AlarmController {

    private final SimpMessageSendingOperations messagingTemplate;


    @MessageMapping("/{userId}")
    public void message(@DestinationVariable("userId") Long userId) {
        messagingTemplate.convertAndSend("/sub/" + userId, "alarm socket connection completed.");
    }



}


