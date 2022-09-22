package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeResponseDto {
    private boolean like;

    public LikeResponseDto(boolean like) {
        this.like = like;

    }
}
