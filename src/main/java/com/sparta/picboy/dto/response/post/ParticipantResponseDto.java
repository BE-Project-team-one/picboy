package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantResponseDto {
    private String username;
    private String nickname;
    private String profileImg;


    public ParticipantResponseDto(String username, String nickname, String profileImg) {
        this.username = username;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }
}
