package com.sparta.picboy.dto.response.mypage;

import lombok.AllArgsConstructor;
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
        this.profilImg = profilImg;
        this.postCount = postCount;
        this.status = status;

        if(status == 2 ) {
            this.nickname = nickname.substring(0,9);
        } else {
            this.nickname = nickname;
        }
    }
}
