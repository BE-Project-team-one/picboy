package com.sparta.picboy.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RandomTopicResponseDto {
    private String topic;

    public RandomTopicResponseDto(String topic){
        this.topic = topic;
    }

}
