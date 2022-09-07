package com.sparta.picboy.service.user;

import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.TokenDto;
import com.sparta.picboy.dto.request.user.LoginRequestDto;
import com.sparta.picboy.dto.request.user.SignupRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.jwt.TokenProvider;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.maven.artifact.repository.Authentication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    // 일반 회원가입
    public ResponseDto<?> signup(SignupRequestDto requestDto) {

        String usernamePattern = "^[A-Za-z0-9]{3,12}$"; // 영어, 숫자 3자이상 12자 이하
        String nicknamePattern = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{2,8}$"; // 영어 , 한글 , 2자이상 8자이하
        String passwordPattern = "(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{4,12}";  // 영어, 숫자, 특수문자 4자이상 12자 이하

        if(!Pattern.matches(usernamePattern, requestDto.getUsername()) ||
           !Pattern.matches(nicknamePattern, requestDto.getNickname()) ||
           !Pattern.matches(passwordPattern, requestDto.getPassword()))
            return ResponseDto.fail("Error Code", "회원가입이 실패했습니다.");

        String password = passwordEncoder.encode(requestDto.getPassword());
        Member member = new Member(requestDto, password);

        memberRepository.save(member);
        return ResponseDto.success("true");
    }

    // 아이디 중복 체크
    public ResponseDto<?> idDoubleCheck(SignupRequestDto requestDto) {

        if(memberRepository.findByUsername(requestDto.getUsername()).isPresent()){
            return ResponseDto.fail("403", "이미 존재하는 아이디입니다.");
            }else{
        }
        return ResponseDto.success("true");
    }
    
    // 닉네임 중복 체크
    public ResponseDto<?> nickDoubleCheck(SignupRequestDto requestDto) {
        if((memberRepository.findByNickname(requestDto.getNickname()).isPresent())){
            return ResponseDto.fail("403", "이미 존재하는 닉네임입니다.");
        }else{
        }
        return ResponseDto.success("true");
    }

    // 휴대폰 인증 코드 보내기


    // 휴대폰 인증 번호 체크


    // 일반 회원 로그인
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {

        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow();

        Pattern.matches(member.getPassword(), requestDto.getPassword());

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        httpServletResponse.addHeader("Access_Token", "Bearer " + tokenDto.getAccessToken());
        httpServletResponse.addHeader("Refresh_Token", tokenDto.getRefreshToken());

        return ResponseDto.success("true");
    }
}
