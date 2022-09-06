package com.sparta.picboy.repository.comment;

import com.sparta.picboy.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
