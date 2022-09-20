package com.sparta.picboy.WebSocket;

import com.sparta.picboy.domain.post.Alert;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.response.post.AlertResponseDto;
import com.sparta.picboy.repository.post.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final AlertRepository alertRepository;


    public void alarmByMessage(MessageDto messageDto) {

        for(Member member : messageDto.getMemberSet()) {
            Alert alert = new Alert(messageDto.getContent(),member);
            alertRepository.save(alert);;
            AlertResponseDto alertResponseDto = new AlertResponseDto(messageDto.getPostId(), messageDto.getContent(), alert.getMember().getUsername());
            messagingTemplate.convertAndSend("/sub/" + member.getUsername(), alertResponseDto);
        }
    }

}
