package com.sparta.picboy.controller.user;


import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.user.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class KakaoController {

    private final KakaoService kakaoService;

    //소셜 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public @ResponseBody ResponseDto<?> kakaoLogin(String code){
        return kakaoService.kakaoLogin(code);
    }
}