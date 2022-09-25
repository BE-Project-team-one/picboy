package com.sparta.picboy.repository.queryDsl;

import com.sparta.picboy.dto.response.user.UserResponseDto;

import java.util.List;

public interface MemberRepositoryCustom {

    // 전체 맴버 찾기
    List<UserResponseDto> findAllMember();

    // 유저 계정 정지
    void userLock(Long memberId);
    // 유저 잠금 해제
    void userClear(Long memberId);

    // 오늘 가입된 회원 수
    int todayRegister();
}
