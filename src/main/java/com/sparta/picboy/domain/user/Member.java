package com.sparta.picboy.domain.user;

import com.sparta.picboy.domain.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private int status; // 로그인 상태

    @Column(unique = true)
    private String phoneNumber; // 휴대폰 번호

    @Column
    private String authority; // 권한

    public Member(String username, String nickname, String password, String profileImg, int status, String phoneNumber, String authority) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.profileImg = profileImg;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.authority = authority;
    }
}
