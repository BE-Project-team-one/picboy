package com.sparta.picboy.repository.queryDsl;

import com.sparta.picboy.dto.response.mypage.MypageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostRepositoryCustom {

    // 아이디로 post 찾기
    Page<MypageResponseDto> categorySort(String nickname, int tabNum, int categoryNum, Pageable pageable);
}
