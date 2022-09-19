package com.sparta.picboy.repository.post;


import com.sparta.picboy.domain.post.HidePost;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HidePostRepository extends JpaRepository<HidePost, Long> {

    Optional<HidePost> findByMemberAndPost(Member member, Post post);

}
