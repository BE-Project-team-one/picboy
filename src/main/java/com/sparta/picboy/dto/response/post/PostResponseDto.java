package com.sparta.picboy.dto.response.post;

import com.sparta.picboy.domain.user.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private Long id; // pk값
    private String topic; // 제시어
    private int frameNum; // 마지막 프레임
    private int frameTotal; // 총 프레임
    private String imgUrl; // 마지막 프레임 이미지
    private LocalDateTime expiredAt; // 삭제일
    private int status; // 상태
    private String gifUrl; // gif 이미지
    private int commentCount; // 댓글 수
    private int likeCount; // 좋아요 수
    private int viewCount; // 조회 수
    private int reportCount; // 신고 수
    private Member member; // 유저
}
