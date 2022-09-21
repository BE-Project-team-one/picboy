package com.sparta.picboy.controller.post;

import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.post.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요
    @PostMapping("/post/like/{postid}")
    public ResponseDto<?> likePost(@PathVariable Long postid, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return postLikeService.likePost(postid, userDetails);

    }

}
