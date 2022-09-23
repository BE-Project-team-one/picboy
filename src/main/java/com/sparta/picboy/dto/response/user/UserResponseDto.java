package com.sparta.picboy.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class UserResponseDto {

    private Long id; // pk값
    private String username; // 아이디
    private String nickname; // 닉네임
    private String profileImg; // 프로필 이미지
    private int status; // 로그인 상태
    private String phoneNumber; // 휴대폰 번호
    private String authority; // 권한
    private Long kakaoId;

    public UserResponseDto(Long id, String username, String nickname, String profileImg, int status, String phoneNumber, String authority, Long kakaoId) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.authority = authority;
        this.kakaoId = kakaoId;
    }
}
