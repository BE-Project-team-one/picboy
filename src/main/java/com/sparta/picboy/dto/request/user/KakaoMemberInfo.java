package com.sparta.picboy.dto.request.user;

import lombok.Getter;

@Getter
public class KakaoMemberInfo{
    private Long KakaoId;
    private String nickname;
    private String email;
    private String profileUrl;

    private String message;

    private boolean response;

    public KakaoMemberInfo(boolean response,String message) {
        this.message = message;
        this.response = response;
    }

    public KakaoMemberInfo(Long KakaoId, String nickname, String email, String profileUrl) {
        this.KakaoId = KakaoId;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
    }

    public KakaoMemberInfo(Long id, String nickname) {
        this.KakaoId = id;
        this.nickname = nickname;
    }
}