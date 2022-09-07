package com.sparta.picboy.dto.request.user;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class SignupRequestDto {

//    @NotBlank
//    @Size(min = 3, max = 12)
//    @Pattern(regexp = "^[A-Za-z0-9]{3,12}$")
    private String username;

//    @NotBlank
//    @Size(min = 3, max = 12)
//    @Pattern(regexp = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{2,8}$")
    private String nickname;

//    @NotBlank
//    @Size(min = 3, max = 12)
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$")
    private String password;

    private String phoneNumber;
}
