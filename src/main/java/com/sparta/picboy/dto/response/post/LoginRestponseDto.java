package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRestponseDto {
    private String Authorization;
    private String RefreshToken;

    public LoginRestponseDto(String Authorization, String RefreshToken) {
        this.Authorization = Authorization;
        this.RefreshToken = RefreshToken;

    }
}
