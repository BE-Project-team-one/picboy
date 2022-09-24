package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostCompletionResponseDto {
    private Long id;
    private String gifUrl;
    private int likeCount;
    private String topic;
    private String nickname;
    private String profileImg;
    private int commentCount;
    private int reportCount;
    private LocalDateTime date; // 생성날짜 = 움짤 완성된 날짜
    private int viewCount;
    private int status; // 반드시 2 의 값을 가짐
    private List<ParticipantResponseDto> participantResponseDtoList;
    private int participantCount;
    private LocalDateTime completAt;

    public PostCompletionResponseDto(Long id, String gifUrl, int likeCount, String topic, String nickname, String profileImg, int commentCount, int reportCount, LocalDateTime date, int viewCount, int status, List<ParticipantResponseDto> participantResponseDtoList, int participantCount, LocalDateTime completAt) {
        this.id = id;
        this.gifUrl = gifUrl;
        this.likeCount = likeCount;
        this.topic = topic;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.commentCount = commentCount;
        this.reportCount = reportCount;
        this.date = date;
        this.viewCount = viewCount;
        this.status = status;
        this.participantResponseDtoList = participantResponseDtoList;
        this.participantCount = participantCount;
        this.completAt = completAt;
    }
}
