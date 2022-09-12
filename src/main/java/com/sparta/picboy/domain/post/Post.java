package com.sparta.picboy.domain.post;

import com.sparta.picboy.domain.Timestamped;
import com.sparta.picboy.domain.user.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk값

    @Column
    private String topic; // 제시어

    @Column(nullable = false)
    private int frameNum; // 마지막 프레임

    @Column(nullable = false)
    private int frameTotal; // 총 프레임

    @Column(nullable = false)
    private String imgUrl; // 마지막 프레임 이미지

    @Column
    private LocalDateTime expiredAt; // 삭제일

    @Column(nullable = false)
    private int status; // 상태

    @Column
    private String gifUrl; // gif 이미지

    @Column
    private int commentCount; // 댓글 수

    @Column
    private int likeCount; // 좋아요 수

    @Column
    private int viewCount; // 조회 수

    @Column
    private int reportCount; // 신고 수

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 유저

    public Post(String topic, int frameNum, int frameTotal, String imgUrl, LocalDateTime expiredAt, int status, String gifUrl, int commentCount, int likeCount, int viewCount, int reportCount, Member member) {
        this.topic = topic;
        this.frameNum = frameNum;
        this.frameTotal = frameTotal;
        this.imgUrl = imgUrl;
        this.expiredAt = expiredAt;
        this.status = status;
        this.gifUrl = gifUrl;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.reportCount = reportCount;
        this.member = member;
    }

    public Post(String topic, int frameNum, int frameTotal, String imgUrl, int status, Member member) {
        this.topic = topic;
        this.frameNum = frameNum;
        this.frameTotal = frameTotal;
        this.imgUrl = imgUrl;
        this.status = status;
        this.member = member;
    }

    public void updateExpiredAt(LocalDateTime date) {
        this.expiredAt = date.plusDays(7);
    }

    public void frameUpdate(int frameNum) {
        this.frameNum = frameNum;
    }
    public void imgUpdate(String imgUrl){
        this.imgUrl = imgUrl;
    }
    public void statusUpdate(int status) {
        this.status = status;
    }

    public void updateGif(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    // comment 개수 더하기빼기(jck)
    public void updateCommentCnt(int commentCount){
        this.commentCount = commentCount;
    }

    // like 개수 더하기빼기
    public void updateLikeCnt(int likeCount) {
        this.likeCount = likeCount;

    }

    // viewCount 업데이트
    public void updateViewCount(int viewCount){
        this.viewCount = viewCount;

    }

}
