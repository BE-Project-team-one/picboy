package com.sparta.picboy.dto.request.user;

import com.sparta.picboy.domain.user.Member;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    private String username;
    private String nickname;
    private String password;
    private String phoneNumber;
    private String email;
}
