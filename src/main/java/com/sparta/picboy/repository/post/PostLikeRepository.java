package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Likes;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<Likes, Long> {

    boolean existsByPostAndMember(Post post, Member member);

    void deleteByPostAndMember(Post post, Member member);

}
