package com.sparta.picboy.service.post;


import com.sparta.picboy.domain.post.HidePost;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.repository.post.HidePostRepository;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HidePostService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final PostRelayRepository postRelayRepository;

    private final HidePostRepository hidePostRepository;

    @Transactional
    public ResponseDto updateHidePost(UserDetails userinfo, Long postId) {
        Member member = memberRepository.findByNickname(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail("NOT_FIND_MEMBER", "유저를 찾을 수 없습니다.");
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );

        Optional<HidePost> optionalHidPost = hidePostRepository.findByMemberAndPost(member, post);
        HidePost hidePost = optionalHidPost.orElse(null);

        if (hidePost == null) {
            hidePost = new HidePost(member, post);
            hidePostRepository.save(hidePost);
            return ResponseDto.success("숨김 등록");
        } else {
            hidePostRepository.delete(hidePost);
            return ResponseDto.success("숨김 취소");
        }

    }
}