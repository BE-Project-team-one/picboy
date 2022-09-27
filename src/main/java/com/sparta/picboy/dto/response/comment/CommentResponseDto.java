package com.sparta.picboy.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String username;
    private String nickname;
    private String comment;
    private String profileImage;
    private LocalDateTime modifiedAtAt;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class inner {
        private List<CommentResponseDto> comment;
    }
}