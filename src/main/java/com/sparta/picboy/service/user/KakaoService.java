package com.sparta.picboy.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.picboy.domain.user.KakaoProfile;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.domain.user.OAuthToken;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${restApi}")
    String restApi;
    @Value("${redirectUri}")
    String redirectUri;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public ResponseDto<?> kakaoLogin(String code) {

        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String ,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id",restApi);
        params.add("redirect_uri",redirectUri);
        params.add("code",code);

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String ,String>> kakaoTokenRequest =
                new HttpEntity<>(params,headers);

        //Http 요청하기 - Post방식으로  - 그리고 Response의 응답 받음
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );


        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        RestTemplate rt2 = new RestTemplate();


        //HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer "+ oAuthToken.getAccess_token());
        headers2.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String ,String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        //Http 요청하기 - Post방식으로  - 그리고 Response의 응답 받음
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        System.out.println(response2.getBody());
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //Memeber 오브젝트 : username, password
        System.out.println(kakaoProfile.getId());
        System.out.println(kakaoProfile.getKakao_account().getProfile().getNickname());

        return ResponseDto.success(response2.getBody());
    }
}