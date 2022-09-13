package com.sparta.picboy.infinityTest;

import com.sparta.picboy.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepsitory extends JpaRepository<Post, Long> {

    Page<Post> findAllByStatus(int status, Pageable pageable);

}
