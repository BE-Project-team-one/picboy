package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String profileImg;


    public ParticipantResponseDto(Long id, String username, String nickname, String profileImg) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }
}
