package com.sparta.picboy.controller.mypage;

import com.sparta.picboy.dto.request.mypage.MypageImageRequestDto;
import com.sparta.picboy.dto.request.mypage.MypageRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.myPage.MyPageService;
import com.sparta.picboy.service.post.HidePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    private final HidePostService hidePostService;


    //숨기기/숨기기 취소
    @PostMapping("/mypage/post-hidden/{postId}")
    public ResponseDto<?> hidePost(@AuthenticationPrincipal UserDetails userinfo,
            @PathVariable Long postId){
        return hidePostService.updateHidePost(userinfo, postId);
    }

    // 마이페이지 게시글 조회
    @GetMapping("/mypage/post/{tabNum}/{categoryNum}")
    public ResponseDto<?> getMypage(@PathVariable int tabNum,
                                 @PathVariable int categoryNum,
                                 @RequestParam String username,
                                 @RequestParam int page,
                                 @RequestParam int size){

        return myPageService.getMypagePost(username, tabNum, categoryNum, page, size);
    }

    //참여자 정보 가져오기
    @GetMapping("/post/join-list/{postId}")
    public ResponseDto<?> joinList(@PathVariable Long postId){
        return myPageService.getPartipants(postId);
    }

    //회원정보 가져오기
    @GetMapping("/mypage/user-info")
    public ResponseDto<?> getUserInfo(@RequestParam String username){
        return myPageService.getUserInfo(username);
    }

    //닉네임 수정
    @PutMapping("/mypage/update-nickname")
    public ResponseDto<?> updateUserInfo(@AuthenticationPrincipal UserDetails userinfo,
                                         @RequestBody MypageRequestDto requestDto){
        return myPageService.updateNickname(userinfo,requestDto);
    }
    //프로필이미지 수정
    @PutMapping("/mypage/update-image")
    public ResponseDto<?> updateUserInfo(@AuthenticationPrincipal UserDetails userinfo,
                                         @RequestBody MypageImageRequestDto requestDto){
        return myPageService.updateimage(userinfo,requestDto);
    }


}
