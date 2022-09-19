package com.sparta.picboy.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoMemberInfo {
    private Long id;
    private String nickname;
    private String email;
}
