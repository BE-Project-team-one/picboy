package com.sparta.picboy.service.post;

import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.domain.post.Likes;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.response.ResponseDto;
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

        Optional<Member> memberCheck = memberRepository.findByUsername(username);
        if (memberCheck.isEmpty()) { // 그럴일은 없지만 혹시나 유저를 찾지 못했을 경우
            return ResponseDto.fail("USER_NOT_FOUND", "로그인한 유저의 정보를 불러올 수 없습니다. 잘못된 접근입니다.");
        }
        Member member = memberRepository.findByUsername(username).orElseThrow();

        Optional<Post> postCheck = postRepository.findById(postid);
        if (postCheck.isEmpty()) { // 게시물이 존재하지 않은경우
            return ResponseDto.fail("POST_NOT_FOUND", "존재하지 않는 게시물 입니다.");
        }
        Post post = postRepository.findById(postid).orElseThrow();


        if(!postLikeRepository.existsByPostAndMember(post, member)) { // 좋아요

            Likes likes = new Likes(member, post);
            postLikeRepository.save(likes);

            List<Likes> postLikeList = postLikeRepository.findAllByPost(post);
            post.updateLikeCnt(postLikeList.size());

            return ResponseDto.success("좋아요");

        } else { // 좋아요 취소

            postLikeRepository.deleteByPostAndMember(post, member);

            List<Likes> postLikeList = postLikeRepository.findAllByPost(post);
            post.updateLikeCnt(postLikeList.size());

            return ResponseDto.success("좋아요 취소");

        }

    }

}
