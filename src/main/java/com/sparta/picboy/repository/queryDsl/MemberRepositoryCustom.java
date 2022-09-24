package com.sparta.picboy.repository.queryDsl;

import com.sparta.picboy.dto.response.user.UserResponseDto;

import java.util.List;

public interface MemberRepositoryCustom {

    // 전체 맴버 찾기
    List<UserResponseDto> findAllMember();

    // 유저 계정 정지
    void userLock(Long memberId);

    void userClear(Long memberId);
}
