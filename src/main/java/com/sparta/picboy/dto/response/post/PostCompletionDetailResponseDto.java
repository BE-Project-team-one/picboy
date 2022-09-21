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
    private int viewCount;
    private int reportCount;
    private List<CommentListResponseDto> commentListResponseDtoList;


    public PostCompletionDetailResponseDto(Long id, int frameTotal, String topic, String gifUrl, LocalDateTime createdAt, List<FrameImgListResponseDto> frameImgList, int likeCount, int viewCount, int reportCount, List<CommentListResponseDto> commentListResponseDtoList) {
        this.id = id;
        this.frameTotal = frameTotal;
        this.topic = topic;
        this.gifUrl = gifUrl;
        this.createdAt = createdAt;
        this.frameImgList = frameImgList;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.reportCount = reportCount;
        this.commentListResponseDtoList = commentListResponseDtoList;
    }
}
