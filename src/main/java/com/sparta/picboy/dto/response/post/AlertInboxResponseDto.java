package com.sparta.picboy.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlertInboxResponseDto {

    private Long postId;
    private String content;
    private String receiver;
    private LocalDateTime createAt;
    private boolean flag;
    private String topic;

}
