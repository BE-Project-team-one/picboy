package com.sparta.picboy.dto.response.mypage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MypageResponseDto {
    private Long postId;
    private String imgUrl; // img
    private String gifUrl; // gif
    private int likeCount; // 정렬기준
    private int commentCount; //정렬기준
    private int viewCount;
    private int reportCount;
    private String topic;
    private String nickname; // 최초 작성자
    private String profileImg; //작성자 이미지
    private int memberCount;
    private LocalDateTime date; // 만료된 날짜 // 정렬 순
    private int status; // 완성/진행/숨김/전체
    private boolean likesFlag; // 좋아요 상태
    private boolean reportFlag; // 신고 상태

}
