package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostMainTopResponseDto {

    private List<PostMainTop3ResponseDto> top3;
    private List<PostMainTop410ResponseDto> top410;

    public PostMainTopResponseDto(List<PostMainTop3ResponseDto> top3, List<PostMainTop410ResponseDto> top410) {
        this.top3 = top3;
        this.top410 = top410;

    }

}
