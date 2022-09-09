package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlertResponseDto {
    private Long postId;
    private String content;
    private String receiver;
    private boolean flag; // true이면 메시지가 정상적으로 전달됨

    public AlertResponseDto(Long postId, String content, String receiver) {
        this.postId = postId;
        this.content = content;
        this.receiver = receiver;
        this.flag = true;
    }
}
