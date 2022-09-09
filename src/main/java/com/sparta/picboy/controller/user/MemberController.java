package com.sparta.picboy.controller.user;

import com.sparta.picboy.dto.request.user.LoginRequestDto;
import com.sparta.picboy.dto.request.user.SignupRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.user.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 일반 회원가입
    @PostMapping("/user/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequestDto requestDto){
        return memberService.signup(requestDto);
    }

    // 아이디 중복 체크
    @GetMapping("/user/id-double-check/{username}")
    public ResponseDto<?> idDoubleCheck(@PathVariable String username){
        return memberService.idDoubleCheck(username);
    }

    // 닉네임 중복 체크
    @PostMapping("/user/nickname-double-check")
    public ResponseDto<?> nickDoubleCheck(@RequestBody SignupRequestDto requestDto){
        return memberService.nickDoubleCheck(requestDto);
    }

    // 휴대폰 인증 코드 보내기


    // 휴대폰 인증 번호 체크


    // 일반 회원 로그인
    @PostMapping("/user/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse httpServletResponse){
        return memberService.login(requestDto, httpServletResponse);
    }
}
