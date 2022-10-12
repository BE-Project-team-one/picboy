package com.sparta.picboy.service.user;

import com.sparta.picboy.domain.user.Certification;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.TokenDto;
import com.sparta.picboy.dto.request.user.CertificationRequestDto;
import com.sparta.picboy.dto.request.user.FindPasswordRequestDto;
import com.sparta.picboy.dto.request.user.LoginRequestDto;
import com.sparta.picboy.dto.request.user.SignupRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.exception.ErrorCode;
import com.sparta.picboy.jwt.TokenProvider;
import com.sparta.picboy.repository.user.CertificationRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final CertificationRepository certificationRepository;
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    @Value("${api_key}")
    private String api_key;

    @Value("${api_secret}")
    private String api_secret;

    @Value("${phoneNum}")
    private String phoneNum;

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
        // 정지된 계정인지 확인
        if (member.getStatus() == 3) {
            return ResponseDto.fail(ErrorCode.SUSPENDED_ACCOUNT);
        }

        // 비밀번호 일치여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            return ResponseDto.fail(ErrorCode.NOT_CORRECT_PASSWORD);
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        httpServletResponse.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpServletResponse.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        httpServletResponse.addHeader("AccessTokenExpiredTime", tokenDto.getAccessTokenExpiresIn().toString());

        return ResponseDto.success(true);
    }

    public ResponseDto<?> findUsername(String phoneNumber) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber).orElse(null);
        if(member == null){
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        }else {
            String username = member.getUsername();
            return ResponseDto.success(username);
        }
    }

    public ResponseDto<?> getTempPw(FindPasswordRequestDto requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElse(null);
        if(member == null){
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        }else if (!member.getPhoneNumber().equals(requestDto.getPhoneNumber())) {
            return ResponseDto.fail(ErrorCode.NOT_CORRECT_PHONENUMBER);
        }else {
            //임시 비밀번호 발급
            Random random  = new Random();
            int leftLimit = 48;
            int rightLimit = 122;
            int stringLength = 10;
            String tempPassword = random.ints(leftLimit, rightLimit +1 )
                    .filter(i -> (i <= 57 || i>= 65) && (i <=90 || i>= 97))
                    .limit(stringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            Message coolsms = new Message(api_key, api_secret);

            // 4 params(to, from, type, text) are mandatory. must be filled
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("to", requestDto.getPhoneNumber());    // 수신전화번호
            params.put("from", phoneNum);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
            params.put("type", "SMS");
            params.put("text", "임시 비밀번호 :"+ "["+tempPassword+"]" + "입니다.");
            params.put("app_version", "test app 1.2"); // application name and version

            try {
                JSONObject obj = (JSONObject) coolsms.send(params);
                System.out.println(obj.toString());
            } catch (CoolsmsException e) {
                System.out.println(e.getMessage());
                System.out.println(e.getCode());
            }
            //임시 비밀번호로 비밀번호 변경
            member.updatePw(passwordEncoder.encode(tempPassword));
            memberRepository.save(member);

            return ResponseDto.success(true);
        }
    }
}
