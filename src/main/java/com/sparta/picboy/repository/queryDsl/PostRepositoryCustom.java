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

    Page<PostCompletionResponseDto> postRead(int tabNum, int categoryNum, Pageable pageable);

    // 주제에 있는 게시글 갯수
    int topicIsNotNullPost();

    // 주제어 없는 게시글 갯수
    int topicIsNullPost();

    // 완성된 게시물 수
    int completePost();

    //진행중인 게시물 수
    int proceedingPost();

    //숨겨진 게시글 수
    int hidnPost();

    // 오늘 생성된 게시글 수
    int todayCreatePost();

    // 오늘 완성된 게시글 수
    int todayCompletePost();

    // 오늘 삭제될 게시물 수
    int todayDeletePost();



}
