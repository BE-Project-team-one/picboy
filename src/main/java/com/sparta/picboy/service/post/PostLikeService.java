package com.sparta.picboy.service.post;

import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.domain.post.Likes;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.post.LikeResponseDto;
import com.sparta.picboy.exception.ErrorCode;
import com.sparta.picboy.repository.post.PostLikeRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    // 게시물 좋아요/취소
    @Transactional
    public ResponseDto<?> likePost(Long postid, UserDetailsImpl userDetails) {

        String username = userDetails.getUsername();
        Member member = memberRepository.findByUsername(username).orElse(null);
        if (member == null) { // 그럴일은 없지만 혹시나 유저를 찾지 못했을 경우
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        }

        Post post = postRepository.findById(postid).orElse(null);
        if (post == null) { // 게시물이 존재하지 않은경우
            return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        }


        if(!postLikeRepository.existsByPostAndMember(post, member)) { // 좋아요

            Likes likes = new Likes(member, post);
            postLikeRepository.save(likes);

            List<Likes> postLikeList = postLikeRepository.findAllByPost(post);
            post.updateLikeCnt(postLikeList.size());

            boolean like = true;
            LikeResponseDto likeResponseDto = new LikeResponseDto(like);

            return ResponseDto.success(likeResponseDto);

        } else { // 좋아요 취소

            postLikeRepository.deleteByPostAndMember(post, member);

            List<Likes> postLikeList = postLikeRepository.findAllByPost(post);
            post.updateLikeCnt(postLikeList.size());

            boolean like = false;
            LikeResponseDto likeResponseDto = new LikeResponseDto(like);

            return ResponseDto.success(likeResponseDto);

        }

    }

}
