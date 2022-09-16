package com.sparta.picboy.dto.response.post;

import com.sparta.picboy.domain.post.PostRelay;
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

    public FrameImgListResponseDto(PostRelay postRelay) {
        this.imgUrl = postRelay.getImgUrl();
        this.frameNum = postRelay.getFrameNum();
        this.nickname = postRelay.getMember().getNickname();
        this.profileimg = postRelay.getMember().getProfileImg();
    }
}
