package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시물 좋아요 순 Top 4 가져오기기
   List<Post> findTop3ByStatusOrderByLikeCountDesc(int status);


   //
   List<Post> findTop10ByStatusOrderByLikeCountDesc(int status);


    // 게시물 1개 post_id 로 찾기
//    Optional<Post> findById(Long postid); // 안쓰는거 나중에 지우기

    // 게시물 status 값으로 가져오고 최신순 정렬
    Page<Post> findAllByStatusOrderByCompletAtDesc(int status, Pageable pageable);

    // 게시물 status 값으로 가져오고 좋아요순 정렬
    Page<Post> findAllByStatusOrderByLikeCountDesc(int status, Pageable pageable);

    // 게시물 status 값으로 가져오고 댓글순 정렬
    Page<Post> findAllByStatusOrderByCommentCountDesc(int status, Pageable pageable);

    // 게시물 status 값으로 가져오고 조회수순 정렬
    Page<Post> findAllByStatusOrderByViewCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 있는것만 가져오기
//    Page<Post> findAllByTopicIsNotNullAndStatusOrderByCompletAtDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 없는것만 가져오기
//    Page<Post> findAllByTopicIsNullAndStatusOrderByCompletAtDesc(int status, Pageable pageable);






    // 게시물 중에서 제시어가 있는것들 중에서 최신순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByCompletAtDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 있는것들 중에서 좋아요순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByLikeCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 있는것들 중에서 댓글순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByCommentCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 있는것들 중에서 댓글순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByViewCountDesc(int status, Pageable pageable);






    // 게시물 중에서 제시어가 없는것들 중에서 최신순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByCompletAtDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 없는것들 중에서 좋아요순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByLikeCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 없는것들 중에서 댓글순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByCommentCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 없는것들 중에서 조회순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByViewCountDesc(int status, Pageable pageable);



    List<Post> findAllByOrderByCreatedAtDesc(); // 최신순



}
