package com.sparta.picboy.dto.response.post;

import com.sparta.picboy.domain.user.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostProceedingResponseDto {

    private Long id;
    private String imgUrl;
    private String topic;
    private Long userid;
    private String username;
    private String nickname;
    private int status;
    private String profileImg;
    private List<ParticipantResponseDto> participantResponseDtoList;
    private int participantCount;

    public PostProceedingResponseDto(Long id, String imgUrl, String topic, Long userid, String username, String nickname, int status, String profileImg, List<ParticipantResponseDto> participantResponseDtoList, int participantCount) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.topic = topic;
        this.userid = userid;
        this.username = username;
        this.nickname = nickname;
        this.status = status;
        this.profileImg = profileImg;
        this.participantResponseDtoList = participantResponseDtoList;
        this.participantCount = participantCount;
    }
}
