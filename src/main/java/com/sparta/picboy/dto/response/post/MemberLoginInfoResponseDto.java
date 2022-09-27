package com.sparta.picboy.dto.response.post;

import com.sparta.picboy.domain.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginInfoResponseDto {

    private String username;
    private String nickname;
    private String profileImg;
    private String authority;

    public MemberLoginInfoResponseDto(String username, String nickname, String profileImg, String authority) {
        this.username = username;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.authority = Authority.ROLE_USER.toString();
    }
}

// 병합 후 유저 리스폰스에 옮겨놓기