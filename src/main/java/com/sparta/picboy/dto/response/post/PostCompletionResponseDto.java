package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostCompletionResponseDto {
    private Long id;
    private String imgUrl;
    private int likeCount;
    private String topic;
    private String nickname;
    private int commentCount;
    private int reportCount;
    private LocalDateTime date; // 생성날짜 = 움짤 완성된 날짜
    private int viewCount;
    private int status; // 반드시 2 의 값을 가짐

    public PostCompletionResponseDto(Long id, String imgUrl, int likeCount, String topic, String nickname, int commentCount, int reportCount, LocalDateTime date, int viewCount, int status) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.likeCount = likeCount;
        this.topic = topic;
        this.nickname = nickname;
        this.commentCount = commentCount;
        this.reportCount = reportCount;
        this.date = date;
        this.viewCount = viewCount;
        this.status = status;
    }
}
