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
    @Transactional //x DB에 저장하는 것이기 때문에 Transactional 을 붙여줘야 함(쉽게 말해서 DB를 거쳐야 한다면 붙여주는게 좋음)
    public ResponseDto<?> signup(SignupRequestDto requestDto) {

        String password = passwordEncoder.encode(requestDto.getPassword()); //x 패스워드 인코딩(암호화)
        Member member = new Member(requestDto, password); //x DB에 저장할 사용자에 대한 정보

        memberRepository.save(member); //x DB에 저장
        return ResponseDto.success("회원가입이 성공했습니다.");
    }

    // 아이디 중복 체크
    public ResponseDto<?> idDoubleCheck(String username) {
        if(memberRepository.findByUsername(username).isPresent()){ //x memberRepository에서 username으로 찾았을 때 username이 존재한다면
            return ResponseDto.fail(ErrorCode.ALREADY_EXIST_USERNAME); //x fail을
            }
        return ResponseDto.success("사용 가능한 아이디입니다."); //x 존재하는 경우 외에는 success를
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
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse httpServletResponse) { //x HttpServlet 사용하는 이유

        // DB에 존재하는 아이디 인지 확인
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElse(null); //x username으로 찾아서 없을 경우를 null로 처리하고 null이 나왔을 때의 에러처리
        if (member == null) {
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);

        }
        // 정지된 계정인지 확인
        if (member.getStatus() == 3) {
            return ResponseDto.fail(ErrorCode.SUSPENDED_ACCOUNT);
        }

        // 비밀번호 일치여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) { //x DB에 존재한다면 passwordEncoder의 matches메소드를 사용하여 비밀번호 비교 (입력한 비밀번호, 저장되어있는 비밀번호)
            return ResponseDto.fail(ErrorCode.NOT_CORRECT_PASSWORD);

        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member); //x 일치한다면 tokenprovider의 generateTokenDto에 member로 매개변수를 이용하여 토큰 생성

        httpServletResponse.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpServletResponse.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        httpServletResponse.addHeader("AccessTokenExpiredTime", tokenDto.getAccessTokenExpiresIn().toString());

        return ResponseDto.success(true);
    }

//    public boolean findPw(String phoneNum, String username) {
//        Member member = memberRepository.findByPhoneNum(phoneNum);
//        if(member!=null &&member.getUsername().equals(username)){
//            return true;
//        }else {
//            return false;
//        }
//    }
}
