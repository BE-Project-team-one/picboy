package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostProceedingResponseDto {

    private PostProceedingMediumDto postProceedingMediumDtoList;

    public PostProceedingResponseDto(PostProceedingMediumDto postProceedingMediumDtoList) {
        this.postProceedingMediumDtoList = postProceedingMediumDtoList;
    }
}
