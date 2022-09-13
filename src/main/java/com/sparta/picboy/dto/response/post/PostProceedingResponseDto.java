package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostProceedingResponseDto {

    private Long id;
    private String imgUrl;
    private String topic;
    private String nickname;
    private int status;
    private String profileImg;
    private List<ParticipantResponseDto> participantResponseDtoList;
    private int participantCount;

    public PostProceedingResponseDto(Long id, String imgUrl, String topic, String nickname, int status, String profileImg, List<ParticipantResponseDto> participantResponseDtoList, int participantCount) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.topic = topic;
        this.nickname = nickname;
        this.status = status;
        this.profileImg = profileImg;
        this.participantResponseDtoList = participantResponseDtoList;
        this.participantCount = participantCount;
    }
}
