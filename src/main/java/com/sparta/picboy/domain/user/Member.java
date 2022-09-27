package com.sparta.picboy.domain.user;

import com.sparta.picboy.domain.Authority;
import com.sparta.picboy.domain.Timestamped;
import com.sparta.picboy.dto.request.user.SignupRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk값

    @Column(nullable = false, unique = true)
    private String username; // 아이디

    @Column(nullable = false, unique = true)
    private String nickname; // 닉네임

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column
    private String profileImg; // 프로필 이미지

    @Column(nullable = false)
    private int status; // 로그인 상태 1.일반 로그인 2.소셜로그인 3.계정 블락

    @Column(unique = true)
    private String phoneNumber; // 휴대폰 번호

    @Column
    private String authority; // 권한

    @Column
    private Long kakaoId;


    public Member(SignupRequestDto requestDto, String password) {  // signup애서 생성자 생성
        this.username = requestDto.getUsername();
        this.nickname = requestDto.getNickname();
        this.password = password;
        this.phoneNumber = requestDto.getPhoneNumber();
        this.authority = Authority.ROLE_USER.toString();
        this.status = 1;
    }

    public Member(String username, String nickname, String encodedPassword, Long kakaoId) {
        this.username = username;
        this.nickname = nickname;
        this.password = encodedPassword;
        this.kakaoId = kakaoId;
        this.authority = Authority.ROLE_USER.toString();
        this.status = 2;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateImg(String profileImg){
        this.profileImg = profileImg;
    }
}
