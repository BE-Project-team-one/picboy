package com.sparta.picboy.service.user;

import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.TokenDto;
import com.sparta.picboy.dto.request.user.LoginRequestDto;
import com.sparta.picboy.dto.request.user.SignupRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.jwt.TokenProvider;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    // 일반 회원가입
    @Transactional
    public ResponseDto<?> signup(SignupRequestDto requestDto) {

        String password = passwordEncoder.encode(requestDto.getPassword());
        Member member = new Member(requestDto, password);

        memberRepository.save(member);
        return ResponseDto.success("회원가입이 성공했습니다.");
    }

    // 아이디 중복 체크
    public ResponseDto<?> idDoubleCheck(String username) {

        if(memberRepository.findByUsername(requestDto.getUsername()).isPresent()){
            return ResponseDto.fail("403", "이미 존재하는 아이디입니다.");
            }else{
        }
        return ResponseDto.success("사용 가능한 아이디입니다.");
    }
    
    // 닉네임 중복 체크
    public ResponseDto<?> nickDoubleCheck(SignupRequestDto requestDto) {
        if((memberRepository.findByNickname(requestDto.getNickname()).isPresent())){
            return ResponseDto.fail("403", "이미 존재하는 닉네임입니다.");
        }else{
        }
        return ResponseDto.success("사용 가능한 닉네임입니다.");
    }

    // 일반 회원 로그인
    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {

        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow();

        if(!Pattern.matches(member.getUsername(),requestDto.getUsername()) ||
        Pattern.matches(member.getPassword(), requestDto.getPassword()))
            ResponseDto.fail("401", "입력 정보가 잘못되었습니다.");

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        httpServletResponse.addHeader("Access_Token", "Bearer " + tokenDto.getAccessToken());
        httpServletResponse.addHeader("Refresh-Token", tokenDto.getRefreshToken());

        return ResponseDto.success("로그인 되었습니다.");
    }
}
