package com.sparta.picboy.domain.comment;

import com.sparta.picboy.domain.Timestamped;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.user.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk 값

    @Column(nullable = false)
    private String comment; // 댓글 내용

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 유저

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post; // 게시물

    public Comment(String comment, Member member, Post post) {
        this.comment = comment;
        this.member = member;
        this.post = post;
    }
}
