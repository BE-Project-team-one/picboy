package com.sparta.picboy.service.user;

import com.sparta.picboy.dto.request.user.CertificationRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.redis.CertificationDao;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class CertificationService {

//    private final CertificationRepository certificationRepository;

    @Value("${api_key}")
    private String api_key;

    @Value("${api_secret}")
    private String api_secret;

    @Value("${phoneNum}")
    private String phoneNum;

//    // 휴대폰 인증 코드 받기
//    @Transactional
//    public ResponseDto<?> certifiedPhoneNumber(CertificationRequestDto requestDto) {
//
//        Random random  = new Random();
//        String numStr = "";
//        for(int i=0; i<4; i++) {
//            String ran = Integer.toString(random.nextInt(10));
//            numStr+=ran;
//        }
//
//        Message coolsms = new Message(api_key, api_secret);
//
//        // 4 params(to, from, type, text) are mandatory. must be filled
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("to", requestDto.getPhoneNum() );    // 수신전화번호
//        params.put("from", phoneNum);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
//        params.put("type", "SMS");
//        params.put("text", "휴대폰 인증 메시지 : 인증번호는" + "["+numStr+"]" + "입니다.");
//        params.put("app_version", "test app 1.2"); // application name and version
//
//        try {
//            JSONObject obj = (JSONObject) coolsms.send(params);
//            System.out.println(obj.toString());
//        } catch (CoolsmsException e) {
//            System.out.println(e.getMessage());
//            System.out.println(e.getCode());
//        }
//
//        Certification certification = certificationRepository.findByPhoneNum(requestDto.getPhoneNum());
//        if (certification == null){
//            certificationRepository.save(new Certification(requestDto.getPhoneNum(), numStr));
//        }else{
//            certification.update(numStr);
//        }
//        return ResponseDto.success("인증 번호가 전송되었습니다.");
//    }
//
//    // 휴대폰 인증 번호 체크
//    @Transactional
//    public ResponseDto<?> verifySms(CertificationRequestDto requestDto) {
//
//        Certification certification = certificationRepository.findByNumStr(requestDto.getNumStr()).orElse(null);
//        if(certification == null || !Pattern.matches(requestDto.getPhoneNum(), certification.getPhoneNum())){
//            return ResponseDto.fail(ErrorCode.NOT_CORRECT_CERTIFINUM);
//        }
//
//        if(Pattern.matches(requestDto.getNumStr(),certification.getNumStr()) &&
//           Pattern.matches(requestDto.getPhoneNum(), certification.getPhoneNum())){
//            certificationRepository.deleteByPhoneNumAndNumStr(requestDto.getPhoneNum(), requestDto.getNumStr());
//        }return ResponseDto.success("인증 번호가 확인 되었습니다.");
//    }

    private final CertificationDao certificationDao;

    // coolSms API를 이용하여 인증번호 발송하고, 발송 정보를 Redis에 저장
    public ResponseDto<?> sendSms(CertificationRequestDto requestDto) {

        Random random  = new Random();
        String numStr = "";
        for(int i=0; i<4; i++) {
            String ran = Integer.toString(random.nextInt(10));
            numStr+=ran;
        }

        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", requestDto.getPhoneNum() );    // 수신전화번호
        params.put("from", phoneNum);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "휴대폰 인증 메시지 : 인증번호는" + "["+numStr+"]" + "입니다.");
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject result = coolsms.send(params);
            if (result.get("success_count").toString().equals("0")) {
                throw new NullPointerException("실패");
            }
        } catch (CoolsmsException exception) {
            exception.printStackTrace();
        }

        certificationDao.createSmsCertification(requestDto.getPhoneNum(), numStr);
        return ResponseDto.success("성공");
    }

    //사용자가 입력한 인증번호가 Redis에 저장된 인증번호와 동일한지 확인
    public ResponseDto<?> verifySms(CertificationRequestDto requestDto) {
        if (isVerify(requestDto)) {
            throw new NullPointerException("인증번호가 일치하지 않습니다.");
        }
        certificationDao.removeSmsCertification(requestDto.getPhoneNum());
        return ResponseDto.success("인증 번호가 확인 되었습니다.");
    }

    private boolean isVerify(CertificationRequestDto requestDto) {
        return (certificationDao.hasKey(requestDto.getPhoneNum()) &&
                certificationDao.getSmsCertification(requestDto.getNumStr())
                        .equals(requestDto.getNumStr()));
    }
}