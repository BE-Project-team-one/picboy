package com.sparta.picboy.service.user;

import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.TokenDto;
import com.sparta.picboy.dto.request.user.LoginRequestDto;
import com.sparta.picboy.dto.request.user.SignupRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.exception.ErrorCode;
import com.sparta.picboy.jwt.TokenProvider;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;


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
        if(memberRepository.findByUsername(username).isPresent()){
            return ResponseDto.fail(ErrorCode.ALREADY_EXIST_USERNAME);
            }
        return ResponseDto.success("사용 가능한 아이디입니다.");
    }
    
    // 닉네임 중복 체크
    public ResponseDto<?> nickDoubleCheck(String nickname) {
        if((memberRepository.findByNickname(nickname).isPresent())){
            return ResponseDto.fail(ErrorCode.ALREADY_EXIST_NICKNAME);
        }
        return ResponseDto.success("사용 가능한 닉네임입니다.");
    }

    // 일반 회원 로그인
    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {

        // DB에 존재하는 아이디 인지 확인
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElse(null);
        if (member == null) {
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);

        }

        // 비밀번호 일치여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            return ResponseDto.fail(ErrorCode.NOT_CORRECT_PASSWORD);

        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        httpServletResponse.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpServletResponse.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        httpServletResponse.addHeader("AccessTokenExpiredTime", String.valueOf(tokenDto.getAccessTokenExpiresIn()));

        // 바디에 토큰값 보내주기
        String authorization = "Bearer " + tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        return ResponseDto.success("로그인 완료");
    }

}
