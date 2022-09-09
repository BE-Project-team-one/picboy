package com.sparta.picboy.service.user;

import com.sparta.picboy.domain.user.Certification;
import com.sparta.picboy.dto.request.user.CertificationRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.repository.user.CertificationRepository;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;

    // 휴대폰 인증 코드 받기
    @Transactional
    public ResponseDto<?> certifiedPhoneNumber(CertificationRequestDto requestDto, String numStr) {
        String api_key = "NCSAW58NB3NRZMYX";
        String api_secret = "W81HQIBFB1MHMS3UMCZIPUBWW3ME5CBC";
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", requestDto.getPhoneNum() );    // 수신전화번호
        params.put("from", "010-8742-9515");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "핫띵크 휴대폰인증 테스트 메시지 : 인증번호는" + "["+numStr+"]" + "입니다.");
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
        certificationRepository.save(new Certification(requestDto.getPhoneNum(), numStr));
        return ResponseDto.success("인증 번호가 전송되었습니다.");
    }

    // 휴대폰 인증 번호 체크
    @Transactional
    public ResponseDto<?> verifySms(CertificationRequestDto requestDto) {

        Certification certification = certificationRepository.findByNumStr(requestDto.getNumStr()).orElse(null);
        if(certification == null || !Pattern.matches(requestDto.getPhoneNum(), certification.getPhoneNum())){
            return ResponseDto.fail("403", "인증 번호가 일치 하지 않습니다.");
        }

        if(Pattern.matches(requestDto.getNumStr(),certification.getNumStr()) &&
           Pattern.matches(requestDto.getPhoneNum(), certification.getPhoneNum())){
            certificationRepository.deleteByPhoneNumAndNumStr(requestDto.getPhoneNum(), requestDto.getNumStr());
        }return ResponseDto.success("인증 번호가 확인 되었습니다.");
    }
}