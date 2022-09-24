package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCompletionResponsePageDto {

    private PostCompletionResponseDto dto;

    public PostCompletionResponsePageDto(PostCompletionResponseDto dto) {
        this.dto = dto;

    }

}
