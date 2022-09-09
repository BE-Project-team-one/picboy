package com.sparta.picboy.service.comment;

import com.sparta.picboy.domain.comment.Comment;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.comment.CommentRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.comment.CommentResponseDto;
import com.sparta.picboy.repository.comment.CommentRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ResponseDto<?> createComment (UserDetails userinfo, Long postId, CommentRequestDto requestDto) {
        Member member = memberRepository.findByNickname(userinfo.getUsername()).orElseThrow(
                ()-> new IllegalArgumentException("유저를 찾을 수 없습니다"));
        if (member == null) return ResponseDto.fail("NOT_FIND_MEMBER", "유저를 찾을 수 없습니다.");

        Post post = postRepository.findById(postId).orElse(null);
        if(post == null){
            return ResponseDto.fail("NOT_FOUND", "게시글이 존재하지 않습니다.");
        }

        String content = requestDto.getContent();

        Comment comment = new Comment(content, member, post);
        commentRepository.save(comment);

        List<Comment> commentList = commentRepository.findAllByPost(post);
        post.updateCommentCnt(commentList.size());
        postRepository.save(post);

        return ResponseDto.success(true);
    }
    @Transactional
    public ResponseDto<?> deleteComment(UserDetails userinfo, Long postId, Long commentId){
        Member member = memberRepository.findByNickname(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail("NOT_FIND_MEMBER", "유저를 찾을 수 없습니다.");

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            return ResponseDto.fail("NOT_FOUND", "댓글이 존재하지 않습니다.");
        }

        Post post = comment.getPost();
        Long writerId = comment.getMember().getId();
        Long userId = member.getId();
        if (!writerId.equals(userId)) {
            return ResponseDto.fail("CANT_DELETE","작성자만 삭제할 수 있습니다.");
        } else {
            commentRepository.delete(comment);
            List<Comment> commentList = commentRepository.findAllByPost(post);
            post.updateCommentCnt(commentList.size());
            postRepository.save(post);
            return ResponseDto.success("삭제되었습니다.");
        }
    }
    @Transactional
    public ResponseDto<?> updateComment(UserDetails userinfo, Long commentId, CommentRequestDto requestDto){
        Member member = memberRepository.findByNickname(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail("NOT_FIND_MEMBER", "유저를 찾을 수 없습니다.");

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            return ResponseDto.fail("NOT_FOUND","댓글이 존재하지 않습니다.");
        }



        Long writerId = comment.getMember().getId();
        Long userId = member.getId();

        if (!writerId.equals(userId)) {
            return ResponseDto.fail("CANT_UPDATE","작성자만 수정할 수 있습니다.");
        } else {
            comment.update(requestDto.getContent());
            commentRepository.save(comment);
            return ResponseDto.success("수정되었습니다.");
        }
    }

    public ResponseDto getComment (Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않은 게시물입니다.");
        }

        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : commentList){
            commentResponseDtos.add(new CommentResponseDto(
                    comment.getPost().getId(),
                    comment.getMember().getNickname(),
                    comment.getComment(),
                    comment.getMember().getProfileImg(),
                    comment.getCreatedAt()));
        }

        CommentResponseDto.inner commentListDto = new CommentResponseDto.inner(commentResponseDtos);

        return ResponseDto.success(commentListDto);
    }





}
