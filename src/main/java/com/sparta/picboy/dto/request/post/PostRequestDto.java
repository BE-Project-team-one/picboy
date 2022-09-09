package com.sparta.picboy.dto.request.post;


import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
public class PostRequestDto {

    private String topic; // 제시어
    private int frameTotal; // 총 프레임

}
