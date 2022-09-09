package com.sparta.picboy.repository.comment;

import com.sparta.picboy.domain.comment.Comment;
import com.sparta.picboy.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

}
