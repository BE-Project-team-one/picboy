package com.sparta.picboy.controller.user;

import com.sparta.picboy.dto.request.user.CertificationRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.user.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Random;


@RestController
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    // 휴대폰 인증 코드 보내기
    @PostMapping("/user/phonenumber-send")
    public ResponseDto<?> sendSMS(@RequestBody CertificationRequestDto requestDto) {
        Random rand  = new Random();
        String numStr = "";
        for(int i=0; i<4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr+=ran;
        }
        return certificationService.certifiedPhoneNumber(requestDto, numStr);
    }

    // 휴대폰 인증 번호 체크
    @PostMapping("/user/code-send")
    public ResponseDto<?> SmsVerification(@RequestBody CertificationRequestDto requestDto) {
        return certificationService.verifySms(requestDto);
    }
}
