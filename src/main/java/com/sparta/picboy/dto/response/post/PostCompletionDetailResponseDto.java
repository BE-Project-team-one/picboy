package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostCompletionDetailResponseDto {
    private Long id;
    private int frameTotal;
    private String topic;
    private String gifUrl;
    private LocalDateTime createdAt;
    private List<FrameImgListResponseDto> frameImgList;
    private int likeCount;
    // 댓글 병합 이후에 이곳에 댓글리스트 추가하기 + 밑에 생성자에도 추가히기


    public PostCompletionDetailResponseDto(Long id, int frameTotal, String topic, String gifUrl, LocalDateTime createdAt, List<FrameImgListResponseDto> frameImgList, int likeCount) {
        this.id = id;
        this.frameTotal = frameTotal;
        this.topic = topic;
        this.gifUrl = gifUrl;
        this.createdAt = createdAt;
        this.frameImgList = frameImgList;
        this.likeCount = likeCount;
    }
}
