package com.sparta.picboy.repository.queryDsl;

import com.sparta.picboy.dto.response.user.UserResponseDto;

import java.util.List;

public interface MemberRepositoryCustom {

    // 전체 맴버 찾기
    List<UserResponseDto> findAllMember();
}
