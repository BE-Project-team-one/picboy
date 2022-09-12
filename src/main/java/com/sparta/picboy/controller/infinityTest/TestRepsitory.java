package com.sparta.picboy.controller.infinityTest;

import com.sparta.picboy.domain.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepsitory extends JpaRepository<Post, Long> {

    List<Post> findAllByStatusOrderByCreatedAtDesc(int status, Pageable pageable);

}
