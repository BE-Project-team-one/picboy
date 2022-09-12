package com.sparta.picboy.dto.response;

import lombok.Getter;

@Getter
public class LoginResponseDto {

    private String authorization;
    private String refreshToken;

    public LoginResponseDto (String authorization, String refreshToken) {
        this.authorization = authorization;
        this.refreshToken = refreshToken;

    }

}
