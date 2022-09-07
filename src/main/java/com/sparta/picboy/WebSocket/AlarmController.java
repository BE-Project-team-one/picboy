package com.sparta.picboy.WebSocket;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class AlarmController {

    private final SimpMessageSendingOperations messagingTemplate;



//    @MessageMapping("/{userId}")
//    public void message(@DestinationVariable("userId") Long userId) {
//        messagingTemplate.convertAndSend("/sub/" + userId, "alarm socket connection completed.");
//    }

    @MessageMapping(value = {"/{roomId}"})
    public void addMessage(@RequestBody MessageRequestDto messageRequestDto, @DestinationVariable Long roomId,
                           @Header("Authorization") String token) {
        token = token.substring(7);

        if(!messageRequestDto.getContent().equals("") && messageRequestDto.getContent() != null) {
            messagingTemplate.convertAndSend("/sub/" + roomId, messageRequestDto);
        }
    }





}


