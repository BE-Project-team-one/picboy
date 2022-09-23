package com.sparta.picboy.repository.queryDsl;

import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.dto.response.mypage.MypageResponseDto;
import com.sparta.picboy.dto.response.post.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostRepositoryCustom {

   // 마이페이지 카테고리별로 게시물 찾기
    Page<MypageResponseDto> categorySort(String username, int tabNum, int categoryNum, Pageable pageable);

    // 전체 게시물 조회
    List<PostResponseDto> findAllPost();
}
