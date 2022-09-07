package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostProceedingDetailResponseDto {
    private Long id;
    private int frameTotal;
    private int frameNum;
    private String topic;
    private String imgUrl;
    private String nickname;
    private String profileImg;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private List<FrameImgListResponseDto> frameImgList;

    public PostProceedingDetailResponseDto(Long id, int frameTotal, int frameNum, String topic, String imgUrl, String nickname, String profileImg, LocalDateTime expiredAt, LocalDateTime createdAt, List<FrameImgListResponseDto> frameImgList) {
        this.id = id;
        this.frameTotal = frameTotal;
        this.frameNum = frameNum;
        this.topic = topic;
        this.imgUrl = imgUrl;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
        this.frameImgList = frameImgList;
    }
}
