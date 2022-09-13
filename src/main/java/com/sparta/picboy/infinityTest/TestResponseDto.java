package com.sparta.picboy.infinityTest;

import com.sparta.picboy.dto.response.post.PostProceedingResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TestResponseDto {

    private boolean isLast;
    private int pageTotalSize;
    private List<PostProceedingResponseDto> postResponseDtoList;

    public TestResponseDto(boolean isLast, int pageTotalSize, List<PostProceedingResponseDto> postResponseDtoList) {
        this.isLast = isLast;
        this.pageTotalSize = pageTotalSize;
        this.postResponseDtoList = postResponseDtoList;
    }
}
