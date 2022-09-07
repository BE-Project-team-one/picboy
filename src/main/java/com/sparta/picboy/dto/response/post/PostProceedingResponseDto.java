package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostProceedingResponseDto {

    private Long id;
    private String imgUrl;
    private String topic;
    private String nickname;
    private int status;

    public PostProceedingResponseDto(Long id, String imgUrl, String topic, String nickname, int status) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.topic = topic;
        this.nickname = nickname;
        this.status = status;

    }

}
