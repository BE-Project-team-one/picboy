package com.sparta.picboy.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.service.user.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/user/kakao")
    public Member kakaoLogin(@RequestParam String code, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, httpServletResponse);
    }
}