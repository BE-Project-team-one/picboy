package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ValidateTokenResponseDto {
    private int validate;

    public ValidateTokenResponseDto(int validate) {
        this.validate = validate;

    }
}
