package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

     // 게시물 좋아요 순 Top 4 가져오기
    List<Post> findTop3ByStatusOrderByLikeCountDescModifiedAtDesc(int status);


    // 게시물 좋아요 순 Top 10 가져오기
    List<Post> findTop10ByStatusOrderByLikeCountDescModifiedAtDesc(int status);


    // 게시물 status 값으로 가져오고 최신순 정렬
    Page<Post> findAllByStatusOrderByModifiedAtDesc(int status, Pageable pageable);






    // 제시어가 있는것들 중에서 진행중인 최신순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByModifiedAtDesc(int status, Pageable pageable);

    // 제시어가 없는것들 중에서 진행중인 최신순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByModifiedAtDesc(int status, Pageable pageable);





    List<Post> findAllByOrderByCreatedAtDesc(); // 최신순
//--------------------
    Page<Post> findAllByStatusOrderByCompletAtDesc(int status, Pageable pageable); //--------------------



}
