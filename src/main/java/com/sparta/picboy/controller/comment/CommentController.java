package com.sparta.picboy.controller.comment;

import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.request.comment.CommentRequestDto;
import com.sparta.picboy.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/comment/{postId}")
    public ResponseDto<?> createComment(@AuthenticationPrincipal UserDetails userinfo,
                                     @PathVariable Long postId,
                                     @RequestBody CommentRequestDto requestDto) {
        return commentService.createComment(userinfo, postId, requestDto);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{postId}/{commentId}")
    public ResponseDto<?> deleteComment(@AuthenticationPrincipal UserDetails userinfo,
                                     @PathVariable Long postId,
                                     @PathVariable Long commentId) {
        return commentService.deleteComment(userinfo, postId, commentId);
    }

    // 댓글 수정
    @PutMapping("/comment/{postId}/{commentId}")
    public ResponseDto<?> updateComment(@AuthenticationPrincipal UserDetails userinfo,
                                     @PathVariable Long commentId,
                                     @RequestBody CommentRequestDto requestDto) {
        return commentService.updateComment(userinfo, commentId, requestDto);
    }

    @GetMapping("/comment/{postId}")
    public ResponseDto<?> getComment(@PathVariable Long postId) {
        return commentService.getComment(postId);
    }

}