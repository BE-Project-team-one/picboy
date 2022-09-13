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
    Page<Post> findAllByStatus(int status, Pageable pageable);

    // 게시물 1개 post_id 로 찾기
    Optional<Post> findById(Long postid);

    // 게시물 status 값으로 가져오고 최신순 정렬
    Page<Post> findAllByStatusOrderByCreatedAtDesc(int status, Pageable pageable);

    // 게시물 status 값으로 가져오고 좋아요순 정렬
    Page<Post> findAllByStatusOrderByLikeCountDesc(int status, Pageable pageable);

    // 게시물 status 값으로 가져오고 댓글순 정렬
    Page<Post> findAllByStatusOrderByCommentCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 있는것만 가져오기
    Page<Post> findAllByTopicIsNotNullAndStatus(int status, Pageable pageable);

    // 게시물 중에서 제시어가 없는것만 가져오기
    Page<Post> findAllByTopicIsNullAndStatus(int status, Pageable pageable);






    // 게시물 중에서 제시어가 있는것들 중에서 최신순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByCreatedAtDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 있는것들 중에서 좋아요순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByLikeCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 있는것들 중에서 댓글순 정렬
    Page<Post> findAllByTopicIsNotNullAndStatusOrderByCommentCountDesc(int status, Pageable pageable);






    // 게시물 중에서 제시어가 없는것들 중에서 최신순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByCreatedAtDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 없는것들 중에서 좋아요순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByLikeCountDesc(int status, Pageable pageable);

    // 게시물 중에서 제시어가 없는것들 중에서 댓글순 정렬
    Page<Post> findAllByTopicIsNullAndStatusOrderByCommentCountDesc(int status, Pageable pageable);


   // mypage-------------------------------------------------------------------------------

   Slice<Post> findAllByOrderByCreatedAtDesc(PageRequest pageRequest); // 무한스크롤
   List<Post> findAllByOrderByCreatedAtDesc(); // 최신순
   List<Post> findAllByOrderByLikeCountDesc(); // 좋아요순
   List<Post> findAllByOrderByCommentCountDesc(); // 댓글순
   List<Post> findAllByMember_NicknameOrderByCreatedAtDesc(String nickname); //최신순(닉네임)
   List<Post> findAllByMember_NicknameOrderByLikeCountDesc(String nickname); //좋아요순(닉네임)
   List<Post> findAllByMember_NicknameOrderByCommentCountDesc(String nickname); //댓글순(닉네임)


    List<Post> findAllByMember_NicknameOrderByCreatedAtDesc(String nickname, Pageable pageable); // 최신순
    List<Post> findAllByMember_NicknameOrderByLikeCountDesc(String nickname, Pageable pageable); // 좋아요순
    List<Post> findAllByMember_NicknameOrderByCommentCountDesc(String nickname, Pageable pageable); // 댓글순


}
