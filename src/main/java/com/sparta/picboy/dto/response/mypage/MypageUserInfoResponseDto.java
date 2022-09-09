package com.sparta.picboy.dto.response.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MypageUserInfoResponseDto {
    private String username;
    private String nickname;
    private String profilImg;
    private int postCount;
}
