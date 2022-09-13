package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시물 좋아요 순 Top 10 가져오기기
   List<Post> findTop10ByStatusOrderByLikeCountDesc(int status);

   // 게시물 status 값으로 분류 전체 가져오기
    List<Post> findAllByStatus(int status);

    // 게시물 1개 post_id 로 찾기
    Optional<Post> findById(Long postid);

    // 게시물 status 값으로 가져오고 최신순 정렬
    List<Post> findAllByStatusOrderByCreatedAtDesc(int status);

    // 게시물 status 값으로 가져오고 좋아요순 정렬
    List<Post> findAllByStatusOrderByLikeCountDesc(int status);

    // 게시물 status 값으로 가져오고 댓글순 정렬
    List<Post> findAllByStatusOrderByCommentCountDesc(int status);

    // 게시물 중에서 제시어가 있는것만 가져오기
    List<Post> findAllByTopicIsNotNullAndStatus(int status);

    // 게시물 중에서 제시어가 없는것만 가져오기
    List<Post> findAllByTopicIsNullAndStatus(int status);






    // 게시물 중에서 제시어가 있는것들 중에서 최신순 정렬
    List<Post> findAllByTopicIsNotNullAndStatusOrderByCreatedAtDesc(int status);

    // 게시물 중에서 제시어가 있는것들 중에서 좋아요순 정렬
    List<Post> findAllByTopicIsNotNullAndStatusOrderByLikeCountDesc(int status);

    // 게시물 중에서 제시어가 있는것들 중에서 댓글순 정렬
    List<Post> findAllByTopicIsNotNullAndStatusOrderByCommentCountDesc(int status);






    // 게시물 중에서 제시어가 없는것들 중에서 최신순 정렬
    List<Post> findAllByTopicIsNullAndStatusOrderByCreatedAtDesc(int status);

    // 게시물 중에서 제시어가 없는것들 중에서 좋아요순 정렬
    List<Post> findAllByTopicIsNullAndStatusOrderByLikeCountDesc(int status);

    // 게시물 중에서 제시어가 없는것들 중에서 댓글순 정렬
    List<Post> findAllByTopicIsNullAndStatusOrderByCommentCountDesc(int status);


   // mypage-------------------------------------------------------------------------------

    List<Post> findAllByOrderByCreatedAtDesc(); // 최신순
    List<Post> findAllByMember_NicknameOrderByCreatedAtDesc(String nickname, Pageable pageable); // 최신순
    List<Post> findAllByMember_NicknameOrderByLikeCountDesc(String nickname, Pageable pageable); // 좋아요순
    List<Post> findAllByMember_NicknameOrderByCommentCountDesc(String nickname, Pageable pageable); // 댓글순


}
