package com.sparta.picboy.controller.user;

import com.sparta.picboy.WebSocket.AlarmService;
import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlarmService alarmService;


    // 알람 읽음 처리 확인
    @GetMapping("/user/alert")
    public ResponseDto<?> readCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        return alarmService.readCheck(userDetails);
    }

    // 알람 전체 읽음 처리
    @PutMapping("/user/alert-update")
    public ResponseDto<?> alertUpdate(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        return alarmService.alertAllRead(userDetails);
    }

    // 내 알람 전체 가져오기
    @GetMapping("/user/alert-get")
    public ResponseDto<?> alertGet(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        return alarmService.alertGet(userDetails);
    }
}
