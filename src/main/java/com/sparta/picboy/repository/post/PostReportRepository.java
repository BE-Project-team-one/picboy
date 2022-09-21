package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.Report;
import com.sparta.picboy.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReportRepository extends JpaRepository<Report, Long> {

    boolean existsByPostAndMember(Post post, Member member);

    void deleteByPostAndMember(Post post, Member member);

    List<Report> findAllByPost(Post post);
}
