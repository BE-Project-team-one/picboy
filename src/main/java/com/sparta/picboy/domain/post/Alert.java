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

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 유저

    public Alert(String content, Member member) {
        this.content = content;
        this.member = member;
    }
}
