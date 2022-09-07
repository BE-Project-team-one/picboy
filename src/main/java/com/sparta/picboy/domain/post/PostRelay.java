package com.sparta.picboy.domain.post;

import com.sparta.picboy.domain.Timestamped;
import com.sparta.picboy.domain.user.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class PostRelay extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk 값

    @Column(nullable = false)
    private int frameNum; // 이어 그리기 순번

    @Column(nullable = false)
    private String imgUrl; // 이미지

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 유저

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post; // 게시물

    public PostRelay(int frameNum, String imgUrl, Member member, Post post) {
        this.frameNum = frameNum;
        this.imgUrl = imgUrl;
        this.member = member;
        this.post = post;
    }

}