package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantResponseDto {
    private String nickname;
    private String profileImg;


    public ParticipantResponseDto(String nickname, String profileImg) {
        this.nickname = nickname;
        this.profileImg = profileImg;
    }
}
