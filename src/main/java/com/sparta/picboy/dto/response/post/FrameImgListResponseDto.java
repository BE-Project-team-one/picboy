package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FrameImgListResponseDto {
    private String imgUrl;
    private int frameNum;
    private String nickname;
    private String profileimg;

    public FrameImgListResponseDto(String imgUrl, int frameNum, String nickname, String profileimg) {
        this.imgUrl = imgUrl;
        this.frameNum = frameNum;
        this.nickname = nickname;
        this.profileimg = profileimg;
    }
}
