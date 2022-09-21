package com.sparta.picboy.controller.post;

import com.sparta.picboy.dto.request.post.PostDelayRequestDto;
import com.sparta.picboy.dto.request.post.PostRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.post.PostWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class PostWriteController {

    private final PostWriteService postWriteService;


    // @RequestPart & @RequestParam 차이점 알기
    // 글쓰기
    @PostMapping("/post")
    public ResponseDto<?> createPost(@AuthenticationPrincipal UserDetails userinfo,
                                     @RequestBody PostRequestDto data) {

        return postWriteService.createPost(userinfo, data);
    }

    //제시어 랜덤 생성
    @GetMapping("/post/random-topic")
    public ResponseDto<?> randomTopic() {
        return postWriteService.randomTopic();
    }

    // 이어 그리기 참여
    @PostMapping("/post/relay/{postid}")
    public ResponseDto<?> relayPost(@PathVariable Long postid,
                                    @RequestBody PostDelayRequestDto postDelayRequestDto,
                                    @AuthenticationPrincipal UserDetails userinfo) {
        return postWriteService.relayPost(postid,postDelayRequestDto,userinfo);
    }

    // 게시물 삭제
    @DeleteMapping("/post/{postId}")
    public ResponseDto<?> postDelete(@PathVariable Long postId) {
        return  postWriteService.postDelete(postId);
    }

}
