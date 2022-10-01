package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRelayRepository extends JpaRepository<PostRelay, Long> {

    List<PostRelay> findAllByPost(Post post);

    boolean existsByMember_UsernameAndPost(String username, Post post);
}

