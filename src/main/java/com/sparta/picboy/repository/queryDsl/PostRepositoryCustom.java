package com.sparta.picboy.repository.queryDsl;

import com.sparta.picboy.dto.response.mypage.MypageResponseDto;
import com.sparta.picboy.dto.response.post.AlertInboxResponseDto;
import com.sparta.picboy.dto.response.post.PostCompletionResponseDto;
import com.sparta.picboy.dto.response.post.PostCompletionResponsePageDto;
import com.sparta.picboy.dto.response.post.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostRepositoryCustom {

   // 마이페이지 카테고리별로 게시물 찾기
    Page<MypageResponseDto> categorySort(String username, int tabNum, int categoryNum, Pageable pageable);

    // 전체 게시물 조회
    List<PostResponseDto> findAllPost();

    // 읽은 게시글 확인
    int readCheckPost(String username);

    // 알람 전체 읽음
    void alertAllRead(String username);

    List<AlertInboxResponseDto> alertAllGet(String username);

    List<PostCompletionResponsePageDto> postRead(int tabNum, int categoryNum, Pageable pageable);

}
