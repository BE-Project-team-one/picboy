package com.sparta.picboy.controller.user;

import com.sparta.picboy.dto.request.user.CertificationRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.user.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

//    // 휴대폰 인증 코드 보내기
//    @PostMapping("/user/phonenumber-send")
//    public ResponseDto<?> sendSMS(@RequestBody CertificationRequestDto requestDto) {
//        return certificationService.certifiedPhoneNumber(requestDto);
//    }
//
//    // 휴대폰 인증 번호 체크
//    @PostMapping("/user/code-send")
//    public ResponseDto<?> SmsVerification(@RequestBody CertificationRequestDto requestDto) {
//        return certificationService.verifySms(requestDto);
//    }

    //인증번호 발송
    @PostMapping("/user/sms-certification/sends")
    public ResponseDto<?> sendSms(@RequestBody CertificationRequestDto requestDto) {
        return certificationService.sendSms(requestDto);
    }

    //인증번호 확인
    @PostMapping("/user/sms-certification/confirms")
    public ResponseDto<?> SmsVerification(@RequestBody CertificationRequestDto requestDto) {
        return certificationService.verifySms(requestDto);
    }
}
