package com.sparta.picboy.WebSocket;

import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.domain.post.Alert;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.post.AlertResponseDto;
import com.sparta.picboy.repository.post.AlertRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.queryDsl.PostRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final AlertRepository alertRepository;
    private final PostRepositoryImpl postRepository;

    private final PostRepository jpaPost;

    // 알람 메시지 보내는 메소드
    public void alarmByMessage(MessageDto messageDto) {
        Post post = jpaPost.findById(messageDto.getPostId()).orElse(null);
        for(Member member : messageDto.getMemberSet()) {
            Alert alert = new Alert(messageDto.getContent(),member, post);
            alertRepository.save(alert);
            AlertResponseDto alertResponseDto = new AlertResponseDto(messageDto.getPostId(), messageDto.getContent(), alert.getMember().getUsername());
            messagingTemplate.convertAndSend("/sub/" + member.getUsername(), alertResponseDto);
        }
    }

    //게시글 중 읽지 않은 메시지가 있는지 확인 -> true 다 읽음, false 다 읽지 않음
    public ResponseDto<?> readCheck(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        int count = postRepository.readCheckPost(username);

        if(count < 1) return ResponseDto.success(true);
        return ResponseDto.success(false);
    }

    // 전체 알람 읽음 처리 update
    @Transactional
    public ResponseDto<?> alertAllRead(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        try {
            postRepository.alertAllRead(username);
            return ResponseDto.success(true);
        } catch (Exception e) {
            return ResponseDto.success(false);
        }

    }

    // 내 알람 전체 가져오기
    public ResponseDto<?> alertGet(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        return ResponseDto.success(postRepository.alertAllGet(username));
    }


}
