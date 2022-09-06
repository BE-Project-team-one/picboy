package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
