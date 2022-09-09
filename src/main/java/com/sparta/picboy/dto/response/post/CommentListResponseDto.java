package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentListResponseDto {

    private Long commentId;
    private String profileImg;
    private String nickname;
    private String comment;
    private LocalDateTime createdAt;

    public CommentListResponseDto(Long commentId, String profileImg, String nickname, String comment, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
