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
public class Alert extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk 값

    @Column(nullable = false)
    private String content; // 알람 내용

    @Column
    private boolean flag;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 유저

    @Column
    private Long postId;

    @Column
    private String topic;

    public Alert(String content, Member member, Long postId, String topic) {
        this.content = content;
        this.member = member;
        this.postId = postId;
        this.flag = false;
        this.topic = topic;
    }
}
