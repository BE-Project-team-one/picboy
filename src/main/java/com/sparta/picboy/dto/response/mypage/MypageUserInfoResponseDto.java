package com.sparta.picboy.dto.response.mypage;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageUserInfoResponseDto {
    private String username;
    private String nickname;
    private String profilImg;
    private int postCount;
    private int status;

    public MypageUserInfoResponseDto(String username, String nickname, String profilImg, int postCount, int status) {
        this.username = username;
        this.nickname = nickname;
        this.profilImg = profilImg;
        this.postCount = postCount;
        this.status = status;

    }
}
