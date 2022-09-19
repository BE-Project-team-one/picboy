package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRelayRepository extends JpaRepository<PostRelay, Long> {

    List<PostRelay> findAllByPost(Post post);

    // 게시물 + 프레임 번호로 찾기
    PostRelay findByPostAndFrameNum(Post post, int frameNum);

    // mypage(JCK)
    boolean existsByMember_IdAndPost(Long id, Post post);
}

